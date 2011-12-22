function AskAndCancel(t)
{
    var answer = confirm("Are you sure you want to cancel this account? You much stop the instance first before cancelling the account. An e-mail will be sent to DuraCloud requesting cancellation.");
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