_**this is out of date**_

#summary Basic Overview

# Things to Do upon downloading #
  * Make sure to update Starcraft Broodwar to 1.16
  * Everything in the `BWSAL_0.9.11\Starcraft` Folder should be moved to your Starcraft folder in Program Files (or wherever it's located)
  * `BWSAL_0.9.11\Windows` files go into the Windows folder
  * Move the `ICCup Maps` folder to your `Starcraft\Maps` Folder

# ProxyBot #

This is intended to be loaded as an Eclipse Project, load in `ProxyBot-2.6.1\ProxyBot` as the folder containing the source when creating the project.

The ProxyBot folder holds most of the required files.

`ProxyBot-2.6.1\ProxyBot\Release` holds the `.dll` files for your `Starcraft\bwapi\AI` folder

Current version uses `ExampleAIModule.dll`

# ChaosLauncher #

in Chaoslauncher folder.  Be sure to run as administrater when trying to inject the BWAPI.

# Running #
  1. Run ProxyBot (in `starcraftbot/proxybot` )
  1. Load up ChaosLauncher (as admin)
  1. Start SC Broodwar
  1. Start Custom Game
  1. Use a Map from the ICCUP Maps Folder
  1. Game should load up after that...