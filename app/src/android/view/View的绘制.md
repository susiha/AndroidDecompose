### View 何时被加载到Window上
一切都要从setContentView这个方法谈起,它调用了Activity的setContentView方法
```
  public void setContentView(@LayoutRes int layoutResID) {
        getWindow().setContentView(layoutResID);
        initWindowDecorActionBar();
    }
```
其中调用了getWindow()的setContentView方法，getWindow()返回的Window对象，其中PhoneWindow是Window的唯一实现类，因为调用了是PhoneWindow的setContentView()方法

```
 @Override
    public void setContentView(int layoutResID) {
        if (mContentParent == null) {
            installDecor();   //////1
        } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
            mContentParent.removeAllViews();
        }

        if (hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
            final Scene newScene = Scene.getSceneForLayout(mContentParent, layoutResID,
                    getContext());
            transitionTo(newScene);
        } else {
           mLayoutInflater.inflate(layoutResID, mContentParent);  ///// 2
        }
        mContentParent.requestApplyInsets();
        final Callback cb = getCallback();
        if (cb != null && !isDestroyed()) {
            cb.onContentChanged();
        }
        mContentParentExplicitlySet = true;
    }
```
这个方法对于我们理解View 是如何被添加上去这个问题 其实就两个地方 分别是上面代码中注释掉的部分，1 安装decorView 2 ，加载本地资源到mContentParent

这里面有两个比较重要的变量，mDecor(这个就是我们常说的decorView)和mContentParent(这个就是我们加载我们自定义资源或者view父控件)

#### installDecor() 这个方法就是初始化 mDecor和mContentParent的

```
 if (mDecor == null) {
            mDecor = generateDecor(-1); //// 1
            mDecor.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            mDecor.setIsRootNamespace(true);
            if (!mInvalidatePanelMenuPosted && mInvalidatePanelMenuFeatures != 0) {
                mDecor.postOnAnimation(mInvalidatePanelMenuRunnable);
            }
        } else {
            mDecor.setWindow(this);
        }
        if (mContentParent == null) {
            mContentParent = generateLayout(mDecor); //// 2
            ....

```

在这个方法里面 就是 mDecor = generateDecor(-1); 初始化decorView,  mContentParent = generateLayout(mDecor); 初始化mContentParent
generateDecor 就是创建一个新的DecorView,

#### generateLayout
```
 mDecor.onResourcesLoaded(mLayoutInflater, layoutResource);
```
首先是根据Windos的属性获取对应的布局文件，比如一般是全屏或者正常的布局，然后调用了DecorView的 onResourcesLoaded方法
#### onResourcesLoaded方法

```
 void onResourcesLoaded(LayoutInflater inflater, int layoutResource) {
        if (mBackdropFrameRenderer != null) {
            loadBackgroundDrawablesIfNeeded();
            mBackdropFrameRenderer.onResourcesLoaded(
                    this, mResizingBackgroundDrawable, mCaptionBackgroundDrawable,
                    mUserCaptionBackgroundDrawable, getCurrentColor(mStatusColorViewState),
                    getCurrentColor(mNavigationColorViewState));
        }

        mDecorCaptionView = createDecorCaptionView(inflater);
        final View root = inflater.inflate(layoutResource, null);
        if (mDecorCaptionView != null) {
            if (mDecorCaptionView.getParent() == null) {
                addView(mDecorCaptionView,
                        new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            }
            mDecorCaptionView.addView(root,
                    new ViewGroup.MarginLayoutParams(MATCH_PARENT, MATCH_PARENT));
        } else {

            // Put it below the color views.
            addView(root, 0, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        }
        mContentRoot = (ViewGroup) root;
        initializeElevation();
    }
```
这个方法就是把根据Window属性把对应的布局添加到DecorView上，这样mContentParent就添加到DecorView，上了，再加上自定义的View 添加到DecorView上，这样自定义的View 就添加到窗口上了

### View的绘制
#### 基本流程
- 在ActivityThread的handleResumeActivity方法中 会有此语句  wm.addView(decor, l);其中这个wm就是windowManager， 而windowManagerImpl是windowManager的实现类，因此，这个方法最终的调用时在WindowManagerImpl的addView(View,LayoutParams)上，
- 而WindowManagerImpl的addView方法又调用了WindowManagerGlobal的add方法
- 在WindowManagerGlobal的addView方法中，首先初始化了ViewRootImpl，然后调用了ViewRootImpl的setView方法
- 从ViewRootimpl的setView开始 调用的顺序 ->requestLayout()->scheduleTraversals（）->doTraversal()->performTraversals()
- 在performTraversals()方法中依次调用了performMeasure（）测量，performLayout（）摆放，performDraw（）绘制
#### performMeasure测量
android 中各个控件在控制之前首先需要知道各个控件的大小，它的测量是从最顶端的View 也就是decorView开始测量的，通过递归其各个子控件从而完成View的测量，在测量的时候借助的是MeasureSpec这个工具类的，了解MeasureSpec能够更好地理解View的测量方法
##### MeasureSpec
MeasureSpec 使用32位的二进制数来表示，其中高两位表示SpecMode,低3O位表示SpecSize





