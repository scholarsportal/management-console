var errMsg = "may not be null";
var divErr = "<div id='instanceNotificationTopicArn.errors' class='error'>" + errMsg + "</div>";
const notifierType = Object.freeze({RABBITMQ:"RabbitMQ", SNS:"SNS"});

function validateSNSform() {
    $("input#instanceNotificationTopicArn").on("input", function(e) {
        if ($(this).val() === "" || $(this).val() === null) {
            $("input#instanceNotificationTopicArn").addClass("error");
             $(divErr).insertAfter(this);
        } else {
            $(this).removeClass("error");
            $(this).siblings("div#instanceNotificationTopicArn.errors").remove();
            $(this).next().remove();
        }
    });
}

$(document).ready(function () {
    if ($("#notifiertype").val() === notifierType.SNS) {
        validateSNSform();
        $(".rabbitmq-config").each(function() {
            $(this).hide();
        });
    } else {
        $(".sns-config").each(function() {
            $(this).hide();
        });
    }

    $("#notifiertype").change(function () {
        if ($(this).val() == notifierType.SNS) {
            validateSNSform();
            $(".rabbitmq-config").each(function() {
                $(this).hide();
            });

            $(".sns-config").each(function() {
                $(this).show();
            });
        } else {
            $(".sns-config").each(function() {
                $(this).hide();
            });
            $(".rabbitmq-config").each(function() {
                $(this).show();
            });
        }
    });

    $("form#globalProperties").submit(function(e) {
        if ( $("#notifiertype").val() === notifierType.SNS ) {
            if ($("input#instanceNotificationTopicArn").val() === "" ||
                $("input#instanceNotificationTopicArn").val() === null) {
                $("input#instanceNotificationTopicArn").addClass("error");
                if ($("input#instanceNotificationTopicArn").next().text() !== errMsg) {
                    $(divErr).insertAfter("input#instanceNotificationTopicArn");
                }

                e.preventDefault();
                return false;
            } else {
                $("input#instanceNotificationTopicArn").removeClass("error");
                $("input#instanceNotificationTopicArn").siblings("div#instanceNotificationTopicArn.errors").remove();
            }
        }
     return true;
    });
});