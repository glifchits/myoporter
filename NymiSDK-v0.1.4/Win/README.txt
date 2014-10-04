Nymi release v0.1.2 for Window and Android
=========================================

Prerequisites
=============
Window: Microsoft Visual Studio 2012 and up
Android: Eclipse with ADT plugin

Installation
=============
To run the Nymulator, the Microsoft 2012 redistributable must be installed on your computer. The redistributable can be found in the Nymulator directory.

HelloNymi
=========
To see if the development environment is properly setup, perform the following steps:
1. Start the Nymulator, which is located in the Nymulator directory
2. Create a new nymi by clicking "new", select "nymi0" from the nymi list, and clicking the right arrow icon next to the label "nymi0"
3. Open in Microsoft Visual Studio ope nthe HelloNymi project file in Examples/HelloNymi/Msvc2012Win/
4. Build and run the project (by pressing the big play button). If the build fails, contact Nikita at dev@bionym.com
5. If the build was successful, you should see a command window pop up that says "Welcome to Hello Nymi!"
7. In the console window, type "Provision" and press enter. You will see a log message saying "log: starting discovery".
8. Go back to the Nymulator app window, and click on the big "Provision" button. In the command window, you should see the output:
log: Nymi discovered
log: stopping scan
log: agreeing
Is this:
01110					// This sequence of bits will be different every time, 
						// and should match the pattern on the red Nymi graphic 
						// in the Nymulator
the correct LED pattern (agree/reject)?
9. If the pattern matches, type agree and press enter. If it doesn't match, contact Nikita at dev@bionym.com.
10. You should see the following additional printouts in the console window:
log: provisioning
log: provisioned
log: disconnected

Testing the provision
---------------------

If everything went well in the previous steps, you can try to connect to the provisioned Nymi as follows:
1. In the command window, type "validate" and press enter. If everything goes well, you should see the following printout:

log: starting finding
log: Nymi found
log: stopping scan
log: validating
Nymi validated! Now trusted user stuff can happen.

This is all for the HelloNymi example. You can now look at the source of the HelloNymi app and see how to write your own Nymi applications.