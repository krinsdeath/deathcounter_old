DeathCounter v0.3.0
===
Count and rank your player and monster kills in Minecraft, and use a convenient command (*/deathcount* or */dc*) to display leaders.

*   by **krinsdeath**

Version 0.3.1
---
*   Switched from iConomy to register; all major economies will now work, including Essentials Economy, iConomy 4 & 5, BOSEconomy 6 and 7, and Multi-currency
*   Code cleanup

Version 0.3.0
---
*   Converted to Superperms
*   Optimized code
*   Fixed possible nullpointers with default values
*   Added aliases for /deathcount - /dc and /deathcounter
*   Converted to maven
Version 0.2.2
---
*   Added iConomy support! (optional)
*   As a result of iConomy, the config file changed. A new flag (settings.iConomy: boolean true|false) has been added as well as a group of keys in iConomy.[mobname]: double
*   Changed it so that SQLite doesn't update the DB as every kill is made. Instead, it uses save_interval in the config.yml to determine when to update all current records in the DB with new values. This should lessen File I/O, and increase performance on large servers drastically.
Version 0.2.0:
*   Finished SQLite support!
*   Added permissions support (optional) - the flag is "deathcounter.admin" and gives access to the /deathcount reset command
*    Reorganized the entire project. The source is very tidy now, and much more module-like. It should allow me to make new features very easily.

