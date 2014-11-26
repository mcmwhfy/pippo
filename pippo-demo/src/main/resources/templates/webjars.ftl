<html>
<head>
    <title>Webjars</title>

    <link href="/webjars/bootstrap/3.0.2/css/bootstrap.min.css" rel="stylesheet">
    <script type="text/javascript">
        $(document).ready(function(){
            $(".close").click(function(){
                $(".alert").alert();
            });
        });
    </script>
</head>
<body style="margin-top: 20px">
    <div class="container">
        <div class="alert alert-info">
            <a href="#" class="close" data-dismiss="alert">&times;</a>
            <strong>Welcome!</strong> Added bootstrap.css using webjars.
        </div>
    </div>
</body>
</html>