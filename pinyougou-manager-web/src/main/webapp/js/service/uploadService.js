app.service("uploadService",function ($http) {

    this.upload=function () {
        var formData=new FormData();
        //获取上传文件的第一个文件， 第一个file文件名
        //file.files[0]  根据id获取文件的第一个文件
        //var file = document.getElementById("file").files[0];
        //formData.append("file",file);
        formData.append("file",file.files[0]);
        //alert(formData.get("file"));

        return $http({
            method:'POST',
            url:"../upload.do",
            data: formData,
            headers: {'Content-Type':undefined},
            transformRequest: angular.identity
        });
    }

})