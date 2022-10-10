# RockerTouchView

[![Download](https://img.shields.io/badge/download-App-blue.svg)](https://raw.githubusercontent.com/jenly1314/CircleProgressView/master/app/release/app-release.apk)
[![API](https://img.shields.io/badge/API-16%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/mit-license.php)
[![Blog](https://img.shields.io/badge/blog-Jenly-9933CC.svg)](https://jenly1314.github.io/)

RockerTouchView for Android 是一Android遥杆组件，支持两种反馈模式。

<div>
        <img src="https://github.com/NicoLiutong/RockerTouchView/blob/main/pay/test.jpg" width="280" heght="350">
    </div>

## RockerTouchView自定义属性说明
| 属性 | 值类型 | 默认值 | 说明 |
| :------| :------ | :------ | :------ |
| innerCircleColor | integer |#C9FFFEFE| 摇杆中间小圆的颜色 |
| outerCircleColor | integer |#BCC8C1C1| 摇杆背景大圆的颜色 |
| directionColor | integer |#FFFFFFFF| 摇杆里边箭头的颜色 |
| isDisplayDirection | boolean | true | 是否显示箭头 |
| returnMode | integer | 0 | 摇杆的反馈模式，0:任意角度 1:固定方向 |
| minIgnoreDistanceRation | integer | 5 | 水平或垂直方向可忽略的最小偏移距离比例（大圆半径和可偏移距离的比值），只有方向模式会调用 |
| proportion | integer | 7 | 小圆的直径和大圆直径的比例，最小是5，最大是10 |

## 引入

### Gradle:

1. 在Project的 **settings.gradle** 里面添加远程仓库  
          
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

2. 在Module的 **build.gradle** 里面添加引入依赖项
```gradle
implementation 'com.github.NicoLiutong:RockerTouchView:v1.0.2'


## 示例

布局示例
```Xml
    <com.nico.rockertouchview.RockerTouchView
        android:id="@+id/rockerTouchView"
        android:layout_width="150dp"
        android:layout_height="150dp"
        android:layout_marginTop="64dp"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:outerCircleColor="@color/outerCircleColor"
        app:innerCircleColor="@color/innerCircleColor"
        app:directionColor="@color/directionColor"
        app:isDisplayDirection="true"
        app:returnMode="1"
        app:minIgnoreDistanceRation="6"
        app:proportion="7"
        />
```

代码示例
```Kotline
    private lateinit var view: RockerTouchView
    
        view = findViewById(R.id.rockerTouchView)
        //设置listener
        view.setRockerTouchViewListener(this)

    //任意角度模式返回的listener
    override fun onAllChange(angle: Int, percent: Float) {
        
    }
    //固定方向模式返回的listener
    override fun onFourChange(direction: RockerTouchView.Direction, percent: Float) {
        
    }

```

更多使用详情，请查看[app](app)中的源码使用示例

## 版本记录
#### v1.0.0：2022-10-09
*  RockerTouchView初始版本

## 赞赏
如果您喜欢RockerTouchView，或感觉RockerTouchView帮助到了您，可以点右上角“Star”支持一下，谢谢 :smiley:<p>
您也可以扫描下面的二维码，请作者喝杯咖啡 :coffee:
    <div>
        <img src="https://github.com/NicoLiutong/RockerTouchView/blob/main/pay/red%20package.jpg" width="280" heght="350">
    </div>
