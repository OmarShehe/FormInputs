# Form Input

![The demo app: typing with input filtering, the classic and French switches, the password checklist and strength meter, a dropdown, the date, time and colour pickers, and the upload fields](https://raw.githubusercontent.com/OmarShehe/FormInputs/master/forminputs.gif)

[![forminput](https://img.shields.io/maven-central/v/io.github.omarshehe/forminput?label=forminput)](https://central.sonatype.com/artifact/io.github.omarshehe/forminput)
[![forminput-views](https://img.shields.io/maven-central/v/io.github.omarshehe/forminput-views?label=forminput-views)](https://central.sonatype.com/artifact/io.github.omarshehe/forminput-views)

Ready-made form inputs: text, price and password fields (with a live rules checklist and strength meter), dropdowns, date, time and colour pickers, and image and document uploads.

## Install

```kotlin
// Compose Multiplatform: Android, desktop JVM, iOS
implementation("io.github.omarshehe:forminput:2.1.0")

// Android Views (XML)
implementation("io.github.omarshehe:forminput-views:1.0.7")
```

## Moved to Maven Central

Releases up to 1.0.6 were published on JitPack as `com.github.OmarShehe:FormInputs`. From 1.0.7 the library is on Maven Central under a new group and artifact name. To update, remove the JitPack repository and change the dependency:

```kotlin
// before
implementation("com.github.OmarShehe:FormInputs:1.0.6")
// after
implementation("io.github.omarshehe:forminput-views:1.0.7")
```

The Compose Multiplatform library is `io.github.omarshehe:forminput`. Kotlin packages are unchanged, so no code changes are needed.

## Try it

`./gradlew :app:installDebug` opens a launcher with a Compose demo of every input and the old View sample. The code to copy is in the demo:

| You want | Look at |
|---|---|
| Every input and the state pattern | [`DemoSections.kt`](app/src/main/java/com/omarshehe/forminputs/DemoSections.kt) |
| A whole form from a list of states | [`DemoGeneratedForm.kt`](app/src/main/java/com/omarshehe/forminputs/DemoGeneratedForm.kt) |
| Style, shape and colours | [`DemoStyle.kt`](app/src/main/java/com/omarshehe/forminputs/DemoStyle.kt) |
| Your own texts or language | [`DemoLocalization.kt`](app/src/main/java/com/omarshehe/forminputs/DemoLocalization.kt) |
| The View (XML) inputs | [`activity_main.xml`](app/src/main/res/layout/activity_main.xml) |

## What you get (Compose)

- **State-driven.** One state class per input; you keep the state and replace it with what `onValueChange` returns.
- **Customisable.** A plain `Modifier` changes any size, `FormInputTheme` sets style and shape for a whole form, and every field takes `textStyle` and `contentPadding`.
- **Password field** with an animated checklist and strength meter; rules can be switched off or replaced.
- **Uploads** with a file-size limit and a type icon per file.
- **Accessible and translated.** Screen-reader names on icon-only controls; built-in English and Swahili, replaceable through `FormInputStrings`.

iOS is compile-checked only; picking a file there reports "not supported".

## Reference

- [`reference.md`](.claude/skills/use-forminput/reference.md): every state, composable and option on one page. Every public composable also has KDoc.

## For AI coding assistants

This repo ships a [Claude Code](https://claude.com/claude-code) skill, [`.claude/skills/use-forminput/`](.claude/skills/use-forminput/SKILL.md). It teaches an assistant which input to pick, the state pattern, styling, form-wide defaults, custom texts and the common mistakes, and it points to [`reference.md`](.claude/skills/use-forminput/reference.md) for exact parameters. Copy the folder into your own project's `.claude/skills/` and the assistant picks it up when you build forms with forminput.

## Build and test

`./gradlew :forminput:jvmTest` runs the library tests. Publishing is described in [`PUBLISHING.md`](PUBLISHING.md).

## License

MIT, see [`LICENSE`](LICENSE).
