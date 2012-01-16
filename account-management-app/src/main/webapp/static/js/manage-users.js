$(document).ready(function() {
    $("#sort").tablesorter({
        headers: {
            4: {
                sorter: false
            },
            5: {
                sorter: false
            },
            6: {
                sorter: false
            },
            7: {
                sorter: false
            },
            8: {
                sorter: false
            },
            9: {
                sorter: false
            }
        }
    });

    $("#sort-accounts").tablesorter({
        headers: {
            4: {
                sorter: false
            },
            5: {
                sorter: false
            },
            6: {
                sorter: false
            }
        }
    });

    $("#sort-images").tablesorter({
        headers: {
            5: {
                sorter: false
            },
            6: {
                sorter: false
            }
        }
    });

    $("#sort-repos").tablesorter({
        headers: {
            7: {
                sorter: false
            },
            8: {
                sorter: false
            }
        }
    });

    $('.hover').click(function() {
        $(this).siblings('.datatablesimple').slideToggle('fast');
    });
 });

function AskAndReset(t)
{
  var answer = confirm("Are you sure you want to reset this user's password?");
  if (answer)
  {
    t.form.submit();
  }
}
function AskAndRemove(t)
{
  var answer = confirm("Are you sure you want to remove this user from the DuraCloud system? This action cannot be undone.");
  if (answer)
  {
    t.form.submit();
  }
}
function AskAndSubmit(t)
{
  var answer = confirm("Are you sure you want to remove the user from this account?");
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
function AskAndDeactivate(t)
{
  var answer = confirm("Are you sure you want to deactivate this account.");
  if (answer)
  {
    t.form.submit();
  }
}
function AskAndRemove(t)
{
  var answer = confirm("Are you sure you want to remove this account from the DuraCloud system? This action cannot be undone.");
  if (answer)
  {
    t.form.submit();
  }
}
function AskAndDel(t)
{
  var answer = confirm("Are you sure you want to remove this Service Repository from the DuraCloud system? This action cannot be undone.");
  if (answer)
  {
    t.form.submit();
  }
}
function AskAndDelete(t)
{
  var answer = confirm("Are you sure you want to remove this Server Image from the DuraCloud system? This action cannot be undone.");
  if (answer)
  {
    t.form.submit();
  }
}