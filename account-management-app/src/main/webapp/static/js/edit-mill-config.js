const queueType = Object.freeze({RABBITMQ:"RabbitMQ", SQS:"SQS"});

$(document).ready(function () {
    if($("#queuetype").val() === queueType.SQS){
        $(".rabbitmq-config").each(function() {
            $(this).hide();
        });
    }
    $("#queuetype").change(function () {
        if ($(this).val() == queueType.SQS) {
            $(".rabbitmq-config").each(function() {
                $(this).hide();
            });
        } else {
            $(".rabbitmq-config").each(function() {
                $(this).show();
            });
        }
    });
});
