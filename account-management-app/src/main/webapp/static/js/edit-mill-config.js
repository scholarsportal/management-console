$(document).ready(function () {
    if($("#queuetype").val() === "AWS"){
        $(".rabbitmq-config").each(function() {
            $(this).hide();
        });
        autofill(".rabbitmq-input", "", "N/A");
    }
    $("#queuetype").change(function () {
        if ($(this).val() == "AWS") {
            $(".rabbitmq-config").each(function() {
                $(this).hide();
            });
            autofill(".rabbitmq-input", "", "N/A");
        }
        else {
            $(".rabbitmq-config").each(function() {
                $(this).show();
            });
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