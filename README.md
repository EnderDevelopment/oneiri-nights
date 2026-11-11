# Minecraft Dream-World Sleep Plugin

OneiriNights is a Paper Minecraft plugin that sends players into a separate dream world when they successfully enter a bed. It spawns three pursuing polar bears and returns the player to their original location when one gets close, or when the player quits, changes worlds, or the server shuts down.

## Install

1. Use a Paper server compatible with Minecraft 1.21.11 and Java 21.
2. Build the project with Maven using `mvn package`.
3. Copy the generated `plugin.jar` from the `target` directory into the server's `plugins` folder.
4. Start or restart the server. The plugin creates a world named `dream_world` if it does not already exist.

## Use

1. Enter a bed successfully to be teleported into the dream world.
2. Avoid the three polar bears that pursue you. Getting within 2.75 blocks of one wakes you and returns you to where you entered the bed.
3. Quitting, leaving the dream world, or shutting down the server also triggers return to your saved location.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/02a0bdee-6ab0-40a2-b772-5a4d85b3b2e0?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=02a0bdee-6ab0-40a2-b772-5a4d85b3b2e0
