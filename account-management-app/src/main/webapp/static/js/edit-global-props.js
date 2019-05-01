$(document).ready(function () {
    if($("#notifiertype").val() === "AWS"){
        $(".rabbitmq-config").each(function() {
            $(this).hide();
        });
        autofill(".rabbitmq-input", "", "N/A");
        autofill(".cloudfront-input", "", "N/A");
    }else{
        $(".aws-config").each(function() {
            $(this).hide();
        });
        autofill(".aws-input", "", "N/A");
        autofill(".cloudfront-input", "", "N/A");
    }
    $("#notifiertype").change(function () {
        if ($(this).val() == "AWS") {
            $(".rabbitmq-config").each(function() {
                $(this).hide();
            });
            $(".aws-config").each(function() {
                $(this).show();
            });
            autofill(".rabbitmq-input", "", "N/A");
            autofill(".aws-input", "N/A", "");
        }
        else {
            $(".aws-config").each(function() {
                $(this).hide();
            });
            $(".rabbitmq-config").each(function() {
                $(this).show();
            });
            autofill(".aws-input", "", "N/A");
            autofill(".rabbitmq-input", "N/A", "");
        }
    });

    function autofill(elemClass, before, after) {
        $(elemClass).each(function() {
            if($(this).val() == before){
                $(this).val(after);
            }
        });
    }
});