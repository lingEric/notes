spring-boot-maven-plugin插件是将springboot的应用程序打包成fat jar的插件。首先我们说一下啥叫fat jar。fat jar 我们暂且叫他胖jar吧,实在是找不到官方叫法了。我们一般的jar，里面放的是.class文件已经resources目录下的东西，但是fat jar 它可以把jar作为内容包含进去。也就是说，spring boot 借助spring-boot-maven-plugin将所有应用启动运行所需要的jar都包含进来，从逻辑上将具备了独立运行的条件。

我们将普通插件maven-jar-plugin生成的包和spring-boot-maven-plugin生成的包unzip，比较一下他们直接的区别，发现使用spring-boot-maven-plugin生成的jar中主要增加了两部分，第一部分是lib目录，这里存放的是应用的Maven依赖的jar包文件,第二部分是spring boot loader相关的类，这个我们下一节再说spring boot 的加载流程。

在项目中需要先加入spring-boot-maven-plugin。

```
           <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>1.3.2.RELEASE</version>
                <configuration>
                    <mainClass>test.ApplicationMain</mainClass>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```

默认是在package阶段执行spring-boot-maven-plugin repackage这个目标。我们看一下RepackageMojo的关键方法execute

```
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (this.project.getPackaging().equals("pom")) {
            getLog().debug("repackage goal could not be applied to pom project.");
            return;
        }
        if (this.skip) {
            getLog().debug("skipping repackaging as per configuration.");
            return;
        }
        //得到项目中的原始的jar，就是使用maven-jar-plugin生成的jar
        File source = this.project.getArtifact().getFile();
        //要写入的目标文件，就是fat jar
        File target = getTargetFile();
        Repackager repackager = new Repackager(source) {
            //从source中寻找spring boot 应用程序入口的main方法。
            @Override
            protected String findMainMethod(JarFile source) throws IOException {
                long startTime = System.currentTimeMillis();
                try {
                    return super.findMainMethod(source);
                }
                finally {
                    long duration = System.currentTimeMillis() - startTime;
                    if (duration > FIND_WARNING_TIMEOUT) {
                        getLog().warn("Searching for the main-class is taking some time, "
                                + "consider using the mainClass configuration "
                                + "parameter");
                    }
                }
            }
        };
        //如果插件中指定了mainClass就直接使用
        repackager.setMainClass(this.mainClass);
        if (this.layout != null) {
            getLog().info("Layout: " + this.layout);
            repackager.setLayout(this.layout.layout());
        }
        //寻找项目运行时依赖的jar，过滤后
        Set<Artifact> artifacts = filterDependencies(this.project.getArtifacts(),
                getFilters(getAdditionalFilters()));
        //将Artifact转化成Libraries 
        Libraries libraries = new ArtifactsLibraries(artifacts, this.requiresUnpack,
                getLog());
        try {
            LaunchScript launchScript = getLaunchScript();
            //进行repackage
            repackager.repackage(target, libraries, launchScript);
        }
        catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
        if (this.classifier != null) {
            getLog().info("Attaching archive: " + target + ", with classifier: "
                    + this.classifier);
            this.projectHelper.attachArtifact(this.project, this.project.getPackaging(),
                    this.classifier, target);
        }
        else if (!source.equals(target)) {
            this.project.getArtifact().setFile(target);
            getLog().info("Replacing main artifact " + source + " to " + target);
        }
    }
```

基本上重要的步骤都有注释，应该不难理解的。再来看下面，当然也不是重点，看看就行。

```
public void repackage(File destination, Libraries libraries,
            LaunchScript launchScript) throws IOException {
        if (destination == null || destination.isDirectory()) {
            throw new IllegalArgumentException("Invalid destination");
        }
        if (libraries == null) {
            throw new IllegalArgumentException("Libraries must not be null");
        }
        if (alreadyRepackaged()) {
            return;
        }
        destination = destination.getAbsoluteFile();
        File workingSource = this.source;
        //如果源jar与目标jar的文件路径及名称是一致的
        if (this.source.equals(destination)) {
            //将源jar重新命名为原名称+.original,同时删除原来的源jar
            workingSource = new File(this.source.getParentFile(),
                    this.source.getName() + ".original");
            workingSource.delete();
            renameFile(this.source, workingSource);
        }
        destination.delete();
        try {
            //将源jar变成JarFile 
            JarFile jarFileSource = new JarFile(workingSource);
            try {
                repackage(jarFileSource, destination, libraries, launchScript);
            }
            finally {
                jarFileSource.close();
            }
        }
        finally {
            if (!this.backupSource && !this.source.equals(workingSource)) {
                deleteFile(workingSource);
            }
        }
    }
```

这一步所做的是清理工作，如果源jar同目标文件路径名称等一致，将源jar重命名，原来的文件删除。为目标文件腾位置。下面的重点来了。

```
    private void repackage(JarFile sourceJar, File destination, Libraries libraries,
            LaunchScript launchScript) throws IOException {
        JarWriter writer = new JarWriter(destination, launchScript);
        try {
            final List<Library> unpackLibraries = new ArrayList<Library>();
            final List<Library> standardLibraries = new ArrayList<Library>();
            libraries.doWithLibraries(new LibraryCallback() {
                @Override
                public void library(Library library) throws IOException {
                    File file = library.getFile();
                    if (isZip(file)) {
                        if (library.isUnpackRequired()) {
                            unpackLibraries.add(library);
                        }
                        else {
                            standardLibraries.add(library);
                        }
                    }
                }
            });
            //按照规则写入manifest文件
            writer.writeManifest(buildManifest(sourceJar));
            Set<String> seen = new HashSet<String>();
            writeNestedLibraries(unpackLibraries, seen, writer);
           //写入源jar中的内容
            writer.writeEntries(sourceJar);
           //写入标准的jar,依赖的jar
            writeNestedLibraries(standardLibraries, seen, writer);
            if (this.layout.isExecutable()) {
               //写入spring boot loader的类
                writer.writeLoaderClasses();
            }
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception ex) {
                // Ignore
            }
        }
    }
```

上面就是一通写，将所需要的内容全部写入到目标文件中。然后就有了我们的fat jar。