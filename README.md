## Tycho Version Sync

Syncs MANIFEST.MF, build.properties and pom.xml for tycho builds.
This is especially handesome if you have a lot of different 
bundles and want to keep poms and manifests in sync.

The current version is just a simple commandline tool. Maybe there
will be an eclipse-plugin or hopefully a tycho-plugin.

## Features

* Sync your _bundle-version_ in the _MANIFEST.MF_ with _pom.xml_ `<version>` element.
* Sync your _exported package_ versions with the _bundle-version_ in the _MANIFEST.MF_ or _pom.xml_
* Update your _imported packages_ / _required bundles_ versions
* Update your _qualifier_ in the _build.properties_ file

## Sample usages

**TODO**
