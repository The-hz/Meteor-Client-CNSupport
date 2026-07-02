# Meteor Client CNSupport

> 基于官方 Meteor Client 的分支，专为 Minecraft Fabric 打造。移除了原本自带的字体，并为 ClickGUI等 增加了完善的中文渲染支持，未来会对模块进行汉化，并尽快跟上原仓库的脚步

##  特性

本分支主要解决了 Meteor Client 在中文环境下字体渲染的痛点，包含以下核心改动：

- **️ ClickGUI 原版字体开关**：在 GUI 主题设置中新增了 `Custom Font` 选项。关闭后，ClickGUI 将使用 Minecraft 原生的字体渲染器（`VanillaTextRenderer`），原生支持中文及 CJK 字符，告别滚木。
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
