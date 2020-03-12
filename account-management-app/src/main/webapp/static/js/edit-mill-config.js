$(document).ready(function () {
    if($("#queuetype").val() === "SQS"){
        $(".rabbitmq-config").each(function() {
            $(this).hide();
        });
    }
    $("#queuetype").change(function () {
        if ($(this).val() == "SQS") {
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

    if($("#auditqueuetype").val() === "SQS"){
        $(".swift-config").each(function() {
            $(this).hide();
        });
    }
    $("#logtype").change(function () {
        if ($(this).val() == "SQS") {
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
