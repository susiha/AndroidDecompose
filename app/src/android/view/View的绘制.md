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
MeasureSpec 使用32位的二进制数来表示，其中高两位表示SpecMode,低30位表示SpecSize
因此MeasureSpec使用0<<30，1<<30,2<<30表示三种测量模式，这表示高两位分别是00，01，10代表各自的测量模式
其中00表示UNSPECIFIED
01表示EXACTLY准确的
10 表示AT_MOST最多
而后30位表示的是测量的大小，通过MeassureSpec可以同时表示两个变量(测量模式，测量大小)

##### performMeasure流程
了解完MeasureSpec之后，看一下具体的测量流程，从performMeasure开始
```
 private void performMeasure(int childWidthMeasureSpec, int childHeightMeasureSpec) {
        try {
            mView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        } finally {
        }
    }
```
直接调用的是mView的measure方法，首先先看看childWidthMeasureSpec 和childHeightMeasureSpec是怎么得到的
```
    int childWidthMeasureSpec = getRootMeasureSpec(mWidth, lp.width);
    int childHeightMeasureSpec = getRootMeasureSpec(mHeight, lp.height);
```
进入大getRootMeasureSpec方法中，其中mWidth和mHeight事window的宽高，lp.width和lp.height是自身布局的layout_width和layout_height
```
  private static int getRootMeasureSpec(int windowSize, int rootDimension) {
        int measureSpec;
        switch (rootDimension) {

        case ViewGroup.LayoutParams.MATCH_PARENT:
            // Window can't resize. Force root view to be windowSize.
            measureSpec = MeasureSpec.makeMeasureSpec(windowSize, MeasureSpec.EXACTLY);
            break;
        case ViewGroup.LayoutParams.WRAP_CONTENT:
            // Window can resize. Set max size for root view.
            measureSpec = MeasureSpec.makeMeasureSpec(windowSize, MeasureSpec.AT_MOST);
            break;
        default:
            // Window wants to be an exact size. Force root view to be that size.
            measureSpec = MeasureSpec.makeMeasureSpec(rootDimension, MeasureSpec.EXACTLY);
            break;
        }
        return measureSpec;
    }
```
可以看到对于MATCH_PARENT来说 mode就是Exactly size就是windowSize 对于WRAP_CONTENT来说 mode就是AT_MOST,size就是windowSize,这样window的measureSpec就测量好了，接着看decorView的测量,接下来走进入的是mView的measure方法
```
 public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
    onMeasure(widthMeasureSpec, heightMeasureSpec);
 }
```
因为measure方法是不能被重写的，因此所有的View都会通过调用measure方法，然后调用到onMeasure方法

```
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

```
onMeasure方法是个可以被重写的方法，不同的View会有不同的测量逻辑，但是如果重写onMeasure方法必须调用setMeasuredDimension方法来保存view的宽高，因为decorView是FrameLayout，我们看看它的onMeasure是如何实现的
```
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount(); //获取子View的个数

        final boolean measureMatchParentChildren =
                MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY ||
                MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;
        mMatchParentChildren.clear();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (mMeasureAllChildren || child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                maxWidth = Math.max(maxWidth,
                        child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                maxHeight = Math.max(maxHeight,
                        child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
                if (measureMatchParentChildren) {
                    if (lp.width == LayoutParams.MATCH_PARENT ||
                            lp.height == LayoutParams.MATCH_PARENT) {
                        mMatchParentChildren.add(child);
                    }
                }
            }
        }

        // Account for padding too
        maxWidth += getPaddingLeftWithForeground() + getPaddingRightWithForeground();
        maxHeight += getPaddingTopWithForeground() + getPaddingBottomWithForeground();

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Check against our foreground's minimum height and width
        final Drawable drawable = getForeground();
        if (drawable != null) {
            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
        }

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));

        count = mMatchParentChildren.size();
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                final View child = mMatchParentChildren.get(i);
                final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                final int childWidthMeasureSpec;
                if (lp.width == LayoutParams.MATCH_PARENT) {
                    final int width = Math.max(0, getMeasuredWidth()
                            - getPaddingLeftWithForeground() - getPaddingRightWithForeground()
                            - lp.leftMargin - lp.rightMargin);
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                            width, MeasureSpec.EXACTLY);
                } else {
                    childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                            getPaddingLeftWithForeground() + getPaddingRightWithForeground() +
                            lp.leftMargin + lp.rightMargin,
                            lp.width);
                }

                final int childHeightMeasureSpec;
                if (lp.height == LayoutParams.MATCH_PARENT) {
                    final int height = Math.max(0, getMeasuredHeight()
                            - getPaddingTopWithForeground() - getPaddingBottomWithForeground()
                            - lp.topMargin - lp.bottomMargin);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                            height, MeasureSpec.EXACTLY);
                } else {
                    childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                            getPaddingTopWithForeground() + getPaddingBottomWithForeground() +
                            lp.topMargin + lp.bottomMargin,
                            lp.height);
                }

                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }
```

首先明确的一点 当View/ViewGroup 调用onMeasure的时候，它的measureSpec就是确定的，因此上面的代码就是首先测量子View的大小，这里面涉及几个方法 measureChildWithMargins()
```
 protected void measureChildWithMargins(View child,
            int parentWidthMeasureSpec, int widthUsed,
            int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                mPaddingLeft + mPaddingRight + lp.leftMargin + lp.rightMargin
                        + widthUsed, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                mPaddingTop + mPaddingBottom + lp.topMargin + lp.bottomMargin
                        + heightUsed, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }
```

```
```


这个方法就是根据父控件的measurSpec以及父控件的padding和子空间的布局属性 获取子空间的measureSpec
```
public static int getChildMeasureSpec(int spec, int padding, int childDimension) {
        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(spec);

        int size = Math.max(0, specSize - padding);

        int resultSize = 0;
        int resultMode = 0;

        switch (specMode) {
        // Parent has imposed an exact size on us
        case MeasureSpec.EXACTLY:
            if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size. So be it.
                resultSize = size;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size. It can't be
                // bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;

        // Parent has imposed a maximum size on us
        case MeasureSpec.AT_MOST:
            if (childDimension >= 0) {
                // Child wants a specific size... so be it
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size, but our size is not fixed.
                // Constrain child to not be bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size. It can't be
                // bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;

        // Parent asked to see how big we want to be
        case MeasureSpec.UNSPECIFIED:
            if (childDimension >= 0) {
                // Child wants a specific size... let him have it
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size... find out how big it should
                // be
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size.... find out how
                // big it should be
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            }
            break;
        }
        //noinspection ResourceType
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }

```
然后计算出子控件的measureSpec,然后在进行子控件的measure





















