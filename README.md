# 中文

# Meteor Client CNSupport

> 基于官方 Meteor Client 的分支，专为 Minecraft Fabric 打造。移除了原本自带的字体，并为 ClickGUI等 增加了完善的中文渲染支持，未来会对模块进行汉化，并尽快跟上原仓库的脚步

##  特性

本分支主要解决了 Meteor Client 在中文环境下字体渲染的痛点，包含以下核心改动：

- **️ ClickGUI 原版字体开关**：在 GUI 主题设置中新增了 `Vanilla Font` 选项。启用后，ClickGUI 将使用 Minecraft 原生的字体渲染器（`VanillaTextRenderer`），原生支持中文及 CJK 字符，告别滚木。
- **️ STB 自定义字体中文字符集扩展**：重写了 `Font.java` 的字体图集打包逻辑。通过引入按需生成的 `charset.txt`（从 MC 和 Meteor 的语言文件中提取），让 STB 自定义字体也能正确渲染游戏内出现的中文。
- **️ 精简内置字体**：移除了原版 Meteor 自带的四个英文字体文件，减小模组体积。

##  安装说明

1. 确保你已安装 **Minecraft 1.21.4** 和 **Fabric Loader**。
2. 下载本仓库的最新 Release 版本（`meteor-client-1.21.4-local.jar`）。
3. 将下载的 `.jar` 文件放入游戏的 `mods` 文件夹中。
4. 确保同时安装了 **Fabric API**。

##  使用方法

### 方案一：使用原版字体渲染（最简单，推荐）
如果你不想折腾字体文件，这是最省事的方法：
1. 进入游戏，按 `Right Shift` 打开 ClickGUI。
2. 在 **General** 选项卡下，找到并开启 **Vanilla Font**。
3. 此时 ClickGUI 的所有文本将使用 MC 原生字体渲染，完美支持中文。

### 方案二：使用自定义字体渲染中文
如果你希望保留 Meteor 的自定义字体外观：
1. 准备一个包含中文字形的 `.ttf` 字体文件（如思源黑体、微软雅黑等）。
2. 将其名字后的类似 `Regular` 的后缀移除。
3. 使用压缩软件打开本模组的源码，进入 `assets/meteor-client/fonts/` 目录。
4. 将字体文件替换进去。
5. 在 `meteordevelopment.meteorclient.renderer.Fonts.java` 中将你的字体名字添加进 `BUILTIN_FONTS` 内
6. 确保 `fonts` 目录下有一个 `charset.txt`（用于告诉模组需要打包哪些中文字符）。
7. 在替换后运行构建（详见“构建项目”部分）

##  给开发者的说明

### 字符集生成
如果你需要重新生成 `charset.txt`（例如游戏更新了大量新方块/物品）：
1. 提取 MC jar 里的 `assets/minecraft/lang/zh_cn.json`。
2. 复制 Meteor 源码里的 `assets/meteor-client/lang/zh_cn.json`。
3. 运行font文件夹内的 Python 脚本，将生成的 `charset.txt` 放入 `src/main/resources/assets/meteor-client/fonts/` 目录。



### 构建项目
```bash
git clone https://github.com/The-hz/Meteor-Client-CNSupport.git
cd Meteor-Client-CNSupport
./gradlew build
```
构建产物位于 `build/libs/` 下，请使用不带 `-all` 后缀的 jar 文件。

##  鸣谢

- [Meteor Development](https://github.com/MeteorDevelopment/meteor-client) - 原版 Meteor Client 的开发团队。
- 所有为 Meteor 汉化做出贡献的社区成员。

##  许可证

本项目基于 [GPL-3.0](LICENSE) 许可证开源，与原版 Meteor Client 保持一致，本文档由GLM-5.2生成

# English

# Meteor Client CNSupport

> A fork of the official Meteor Client, built for Minecraft Fabric. Removes the bundled fonts and adds comprehensive Chinese rendering support for ClickGUI and more. Future plans include module localization and keeping up with the upstream repository.

## Features

This branch primarily addresses the pain points of font rendering in Chinese environments for Meteor Client, including the following core changes:

- **️ ClickGUI Vanilla Font Toggle**: Added a `Vanilla Font` option in the GUI theme settings. When enabled, ClickGUI will use Minecraft's native font renderer (`VanillaTextRenderer`), which natively supports Chinese and CJK characters—say goodbye to tofu blocks.
- **️ STB Custom Font CJK Charset Extension**: Rewrote the font atlas packing logic in `Font.java`. By introducing an on-demand generated `charset.txt` (extracted from MC and Meteor language files), STB custom fonts can now correctly render Chinese characters encountered in-game.
- **️ Trimmed Built-in Fonts**: Removed the four built-in English font files from the original Meteor to reduce mod size.

## Installation

1. Ensure you have **Minecraft 1.21.4** and **Fabric Loader** installed.
2. Download the latest Release version from this repository (`meteor-client-1.21.4-local.jar`).
3. Place the downloaded `.jar` file into your game's `mods` folder.
4. Ensure **Fabric API** is also installed.

## Usage

### Option 1: Use Vanilla Font Rendering (Easiest, Recommended)
If you don't want to mess with font files, this is the most hassle-free method:
1. Enter the game and press `Right Shift` to open ClickGUI.
2. Under the **General** tab, find and enable **Vanilla Font**.
3. All text in ClickGUI will now be rendered using MC's native font, with perfect Chinese support.

### Option 2: Use Custom Font for Chinese Rendering
If you wish to retain Meteor's custom font appearance:
1. Prepare a `.ttf` font file that includes Chinese glyphs (e.g., Source Han Sans, Microsoft YaHei, etc.).
2. Remove suffixes like `Regular` from the filename.
3. Open the mod's source archive with a compression tool and navigate to `assets/meteor-client/fonts/`.
4. Replace the font file in that directory.
5. Add your font name to the `BUILTIN_FONTS` array in `meteordevelopment.meteorclient.renderer.Fonts.java`.
6. Ensure there is a `charset.txt` in the `fonts` directory (used to tell the mod which Chinese characters to pack).
7. Run the build after replacement (see "Building the Project" section for details).

## Notes for Developers

### Charset Generation
If you need to regenerate `charset.txt` (e.g., after a game update adds many new blocks/items):
1. Extract `assets/minecraft/lang/zh_cn.json` from the MC jar.
2. Copy `assets/meteor-client/lang/zh_cn.json` from the Meteor source.
3. Run the Python script in the font folder, and place the generated `charset.txt` into `src/main/resources/assets/meteor-client/fonts/`.

### Building the Project
```bash
git clone https://github.com/The-hz/Meteor-Client-CNSupport.git
cd Meteor-Client-CNSupport
./gradlew build
```
The build artifact is located under `build/libs/`; please use the jar file **without** the `-all` suffix.

## Credits

- [Meteor Development](https://github.com/MeteorDevelopment/meteor-client) - The original Meteor Client development team.
- All community members who have contributed to Meteor's Chinese localization.

## License

This project is open-sourced under the [GPL-3.0](LICENSE) license, consistent with the original Meteor Client. This document was generated by GLM-5.2.
