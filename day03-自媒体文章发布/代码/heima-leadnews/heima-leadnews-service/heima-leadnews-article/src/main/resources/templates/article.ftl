<!DOCTYPE html>
<html style="font-size: 42px;">

<head>
    <meta charset="utf-8">
    <title>article_template</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <style>
        .content {
            width: 670px;
            flex-direction: column;
            /*font-size: 0.4rem;*/
            font-size: 30px;
            justify-content: flex-start;
            margin-top: 0.26667rem;
            word-wrap: break-word;
            text-align: justify;
            font-family: "\5FAE\8F6F\96C5\9ED1";
        }

        .text {
            margin: 0.2rem 0px;
            color: #3A3A3A;
        }

        .image {
            display: inline-block;
            margin: 15px 0px;
            border-radius: 5px;
            width: 100%;
            /* height: 300px; */
        }
    </style>
</head>

<body>
    <div class="content">
        <#if content??>
            <#list content as item>
                <#if item.type=="text">
                    <p class="text">
                        ${item.value}
                    </p>
                <#else>
                    <img class="image" src="${item.value}" />
                </#if>
            </#list>
        </#if>
    </div>
</body>

</html>
