function AskAndCancel(t)
{
    var answer = confirm("Are you sure you want to cancel this account? Canceling this account will remove all stored data. Clicking OK will begin the cancellation process by notifying DuraCloud staff. Cancellation will only proceed if the account instance has first been stopped.");
    if (answer)
    {
        t.form.submit();
    }
}

function AskAndDisable(t)
{
  var answer = confirm("Are you sure you want to disable Reduced Redundancy Storage? Changing this setting requires your instance to be reinitialized if it is running.");
  if (answer)
  {
    t.form.submit();
  }
}

function AskAndEnable(t)
{
  var answer = confirm("Are you sure you want to enable Reduced Redundancy Storage? Changing this setting requires your instance to be reinitialized if it is running.");
  if (answer)
  {
    t.form.submit();
  }
}