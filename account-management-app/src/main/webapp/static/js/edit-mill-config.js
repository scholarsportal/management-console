$(document).ready(function () {
    if($("#queuetype").val() === "AWS"){
        $(".rabbitmq-config").each(function() {
            $(this).hide();
        });
    }
    $("#queuetype").change(function () {
        if ($(this).val() == "AWS") {
            $(".rabbitmq-config").each(function() {
                $(this).hide();
            });
        }
        else {
            $(".rabbitmq-config").each(function() {
                $(this).show();
            });
        }
    });

    if($("#logtype").val() === "AWS"){
        $(".swift-config").each(function() {
            $(this).hide();
        });
    }
    $("#logtype").change(function () {
        if ($(this).val() == "AWS") {
            $(".swift-config").each(function() {
                $(this).hide();
            });
        }
        else {
            $(".swift-config").each(function() {
                $(this).show();
            });
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
