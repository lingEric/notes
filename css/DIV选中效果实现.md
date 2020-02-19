DIV选中效果实现

```css
.box{
    position: relative;
    width: 120px;
    height: 120px;
    overflow: hidden;
    background-color: yellow;
    margin: 50px;
    border: 3px solid #4390df;
}

.box:before{
    position: absolute;
    display: block;
    border-top: 28px solid #4390df;
    border-left: 28px solid transparent;
    right: 0;
    top: 0;
    content: "";
    z-index: 101;
}
.box:after{
    position: absolute;
    display: block;
    content: "\e013";            
    top: 0;
    right: 0;
    font-family: Glyphicons Halflings;
    font-size: 10pt;
    font-weight: normal;
    z-index: 102;
    color: #fff;
}
```

