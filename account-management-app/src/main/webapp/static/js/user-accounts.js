
$(document).ready(function(){
    $(".start").click(function(event) {
        var button = $(this);
        $(button).hide();
        $(button).after("    Instance is starting...");
    });

    $(".available").click(function() {
        var button = $(this);
        var id = $(button).attr("id");

        var checking = "#checking-" + id;
        var name = "#instance-available-" + id;
        var form = $(name);

        $.ajax({
          type: "POST",
          url: $(form).attr("action"),
          data: $(form).serialize(),
          success: function() {
            window.location.reload(true);
          },
          error: function() {
            $(button).show();
            $(checking).hide();
          }
        });

        $(checking).show();
        $(this).hide();

        return false;
    });

    $('.available').click();

});

function AskAndDeactivate(t)
{
    var answer = confirm("Are you sure you want to deactivate this account.");
    if (answer)
    {
        t.form.submit();
    }
}

function AskAndSubmit(t)
{
  var answer = confirm("Are you sure you want to stop your instance? Stopping an instance will stop the services you have running.");
  if (answer)
  {
    t.form.submit();
  }
}
function AskAndSubmitUpgrade(t)
{
  var answer = confirm("Are you sure you want to upgrade your instance? This will stop your current instance which will stop the services you have running.");

  if (answer)
  {
    var button = $(t);
    var id = $(button).attr("id");

    var name = "#instance-upgrade-form-" + id;
    var form = $(name);

    $.ajax({
      type: "POST",
      url: $(form).attr("action"),
      data: $(form).serialize(),
      success: function() {
        window.location.reload(true);
      }
    });

    $(button).replaceWith("");

    var replace = "#duradmin-link-" + id;
    $(replace).replaceWith("Upgrading Instance...");

    return false;
  }
}

function AskAndSubmitInit(t)
{
  var answer = confirm("Are you sure you want to re-init your instance?");
  if (answer)
  {
    t.form.submit();
  }
}

function AskAndSubmitInitUsers(t)
{
  var answer = confirm("Are you sure you want to re-init the users?");
  if (answer)
  {
    t.form.submit();
  }
}

function AskAndActivate(t)
{
  var answer = confirm("Are you sure you want to activate this account.");
  if (answer)
  {
    t.form.submit();
  }
}


$(function(){
	var retrieveProperty = function(property, element){
		var that = element;
		var accountId = $(element).attr("data-account-id");
		
		$.ajax({
			url: window.location + "/accounts/"+ accountId + "/instance/"+property,
 	 	    beforeSend: function(){
 	 	    	$(that).siblings().html("loading...");
 	 	    }
		}).done(function(data) {
			$(that).siblings().html(data);
		}).fail(function(data) {
			alert('failed to retrieve data:' + data);
		});

	};
	
	$(".instance-status").click(function(){ retrieveProperty("status", this)});
	$(".instance-type").click(function(){ retrieveProperty("type", this)});

});