# 设置构建本地库所需的最小版本的cbuild。
cmake_minimum_required(VERSION 3.4.1)

# 添加子目录
add_subdirectory(
        src/main/cpp/bsdiffpatch
        src/main/cpp/bzip2
)


# 创建并命名一个库，将其设置为静态
# 或者共享，并提供其源代码的相对路径
# 您可以定义多个库，而cbuild为您构建它们
# Gradle自动将共享库与你的APK打包

# STATIC、SHARED和MODULE的作用是指定生成的库文件的类型
# STATIC库是目标文件的归档文件，在链接其它目标的时候使用
# SHARED库会被动态链接（动态链接库），在运行时会被加载
# MODULE库是一种不会被链接到其它目标中的插件，但是可能会在运行时使用dlopen-系列的函数
# 默认状态下，库文件将会在于源文件目录树的构建目录树的位置被创建，该命令也会在这里被调用

add_library(
        native-lib SHARED
)

# 添加.h文件目录 主要针对cpp工程引用的其他库 .a .so等
include_directories(src/main/cpp/include/)

# 设置目标语言
SET_TARGET_PROPERTIES(native-lib PROPERTIES LINKER_LANGUAGE CXX)

#搜索指定的预构建库，并将该路径存储为一个变量。因为cbuild默认包含了搜索路径中的系统库，所以您只需要指定您想要添加的公共NDK库的名称。cbuild在完成构建之前验证这个库是否存在。
find_library(
        log-lib  # 设置path变量的名称。
        log   #  指定NDK库的名称 你想让CMake来定位。
)
#指定库的库应该链接到你的目标库。您可以链接多个库，比如在这个构建脚本中定义的库、预构建的第三方库或系统库。
target_link_libraries(
        native-lib     #指定目标库中。与 add_library的库名称一定要相同
        bsdiffpatch
        bzip2
        ${log-lib}    # 将目标库链接到日志库包含在NDK。
)
#如果需要生产多个SO文件的话，写法如下
#add_library( natave-lib  #设置库的名称。另一个so文件的名称
#                SHARED  # 将库设置为共享库。
#                src/main/jni/nataveJni.cpp    # 提供一个源文件的相对路径
#              )
#target_link_libraries( natave-lib     #指定目标库中。与 add_library的库名称一定要相同
#                       ${log-lib}    # 将目标库链接到日志库包含在NDK。
#                        )
