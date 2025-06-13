# 🎧 English Word Dictation Program

一个基于 Java Swing 开发的英语单词听写程序，支持：

- 自定义添加/删除单词  
- 保存和导入 `.txt` 文件  
- 美音 / 英音发音选择（调用有道词典 API）  
- 随机播放单词、设置听写间隔时间  
- 拖拽导入文件  
- 支持 Delete 键删除选中单词  
- 播放过程中可点击按钮中途停止  

---

# ✨ 功能特性

## 🧠 单词管理
- ✅ 添加新单词
- ✅ 删除选中单词（按钮或 `Delete` 键）
- ✅ 拖拽 `.txt` 文件导入单词列表
- ✅ 避免重复添加相同单词

## 🔊 听写功能
- ✅ 支持有道词典在线发音（英音/美音）
- ✅ 自定义听写间隔时间（单位毫秒）
- ✅ 支持随机顺序播放
- ✅ 听写过程中高亮当前单词
- ✅ 播放按钮在播放中变为“停止”，支持中途终止播放

## 💾 文件操作
- ✅ 单词列表保存为 `.txt` 文件
- ✅ 从 `.txt` 文件导入单词（支持拖拽）
- ✅ 最近导入或播放成功弹窗提示

## 🧰 交互体验

- ✅ 按下回车即可快速添加单词
- ✅ 拖拽导入自动识别文本文件
- ✅ 播放前检查是否为空、是否输入合法时间
- ✅ 支持跨平台运行（JAR 形式）



---

# 📷 功能界面预览

> 👉
>
> ![image-20250613160300874](C:\Users\IASNH\AppData\Roaming\Typora\typora-user-images\image-20250613160300874.png)![image-20250613160719552](C:\Users\IASNH\AppData\Roaming\Typora\typora-user-images\image-20250613160719552.png)
>
> ![image-20250613160504150](C:\Users\IASNH\AppData\Roaming\Typora\typora-user-images\image-20250613160504150.png)
>
> 






---

# 🚀 如何运行程序

### 方法一：直接运行打包好的 JAR 文件

1. 安装 Java 21（JDK）
2. 下载 `Poem.jar`
3. 打开终端或命令提示符，运行：

```bash
java -jar Poem.jar
```

### 方法二：在 IDEA 中运行源码

1. 使用 IntelliJ IDEA 打开项目
2. 确保 JDK 设置为 21
3. 运行主类 `WordDictationApp.java`



------

## 🔧 使用 IntelliJ IDEA 打包为 JAR

1. IDEA → File → Project Structure
2. 进入 `Artifacts` → `+` → `Jar` → `From modules with dependencies`
3. 选择主类：`WordDictationApp`
4. 应用后点击：`Build` → `Build Artifacts` → `Build`
5. JAR 文件生成于：`out/artifacts/.../`



------

# 📦 注意事项

- 播放音频依赖网络访问有道词典接口：

  ```bash
  http://dict.youdao.com/dictvoice?type=0|1&audio=word
  ```

- 若无法播放，请检查网络或代理设置。
