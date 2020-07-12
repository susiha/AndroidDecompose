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






