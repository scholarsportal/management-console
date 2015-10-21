function AskAndCancel(t)
{
    Ask("Are you sure you want to cancel this account? Cancellation will only proceed if the account instance has first been stopped.",t);
}

function Ask(message,t)
{
  var answer = confirm(message);
  if (answer)
  {
    t.form.submit();
  }
}