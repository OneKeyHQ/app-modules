package com.margelo.nitro.onekeyimage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.test.InstrumentationTestCase
import android.os.SystemClock
import android.view.View
import android.widget.FrameLayout
import android.graphics.drawable.BitmapDrawable
import com.facebook.react.bridge.BridgeReactContext
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.soloader.SoLoader
import com.caverock.androidsvg.SVG
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.ExecutorService
import java.util.concurrent.ThreadPoolExecutor

class OneKeyIconTestActivity : Activity()

@Suppress("DEPRECATION")
class OneKeyIconDecoderTest : InstrumentationTestCase() {
  fun testReactPackageRegistersImageAndIconExactlyOnce() {
    instrumentation.runOnMainSync {
      SoLoader.init(instrumentation.targetContext, OpenSourceMergedSoMapping)
      val context = BridgeReactContext(instrumentation.targetContext)
      val names = OneKeyImagePackage().createViewManagers(context).map { it.name }
      assertEquals(listOf("OneKeyImage", "OneKeyIcon"), names)
      assertEquals(names.size, names.distinct().size)
    }
  }

  fun testNamedCSSAlphaIsExactAndFixedColorsRemain() {
    fun resolve(name: String,alpha: String): Bitmap {
      val latch=CountDownLatch(1)
      val output=java.util.concurrent.atomic.AtomicReference<Bitmap>()
      OneKeyNamedSVG.request(Any(),instrumentation.targetContext.assets,name,"rgba(255,0,0,$alpha)",96,96,1f) { output.set(it);latch.countDown() }
      assertTrue(latch.await(5,TimeUnit.SECONDS));return requireNotNull(output.get())
    }
    for ((alpha,expected) in listOf("0" to 0,"0.5" to 128,"1" to 255,"0.5019607843137255" to 128)) {
      val image=resolve("DotVerSolid",alpha)
      val pixel=image.getPixel(48,48)
      assertEquals("CSS alpha $alpha",expected,Color.alpha(pixel))
      if(expected>0) {assertEquals(255,Color.red(pixel));assertEquals(0,Color.green(pixel));assertEquals(0,Color.blue(pixel))}
      println("NAMED_CSS_ALPHA input=$alpha pixel=${Integer.toHexString(pixel)}")
    }
    val mixed=resolve("OnekeyTextIllus","0")
    val pixels=IntArray(96*96);mixed.getPixels(pixels,0,96,0,0,96,96)
    assertTrue(pixels.any {Color.alpha(it)==255 && Color.green(it)>Color.red(it)})
    assertFalse(pixels.any {Color.alpha(it)>0 && Color.red(it)>Color.green(it)})
  }

  fun testAllBundledSDKFixturesAndAlpha() {
    val context = instrumentation.targetContext
    val names = context.assets.list("icons")!!.filter { it.endsWith(".svg") }.sorted()
    assertEquals(58, names.size)
    val failures = mutableListOf<String>()
    val output = File(context.filesDir, "icon-probe").apply { mkdirs() }
    val representatives = setOf("GooglePayIllus.svg", "ShortcutsCustom.svg", "SidebarLeftArrowCustom.svg", "PassKeySolid.svg", "BotIllus.svg", "OnekeyPrimeLightColored.svg", "AppleBrand.svg", "ArrowTopRightIllus.svg", "OnekeyTextIllus.svg", "CheckboxCheckedCustom.svg", "ShieldDeviceDarkIllus.svg", "CoinsIllus.svg", "MaticCircleIllus.svg", "UserAvatarFallbackColored.svg")
    for (name in names) {
      try {
        val xml = context.assets.open("icons/$name").bufferedReader().use { it.readText() }
          .replace("currentColor", "rgba(255,0,0,0.5)")
        val svg = SVG.getFromString(xml)
        svg.setDocumentWidth(128f)
        svg.setDocumentHeight(128f)
        val bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888)
        svg.renderToCanvas(Canvas(bitmap))
        if (representatives.contains(name)) FileOutputStream(File(output, "$name.png")).use {
          bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
      } catch (error: Exception) { failures.add("$name: ${error.message}") }
    }
    assertEquals(failures.joinToString("\n"), 0, failures.size)
    val alpha = SVG.getFromString("<svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24'><rect width='12' height='24' fill='rgba(255,0,0,0.5)'/><rect x='12' width='12' height='24' fill='#00cc66'/></svg>")
    val bitmap = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888)
    alpha.renderToCanvas(Canvas(bitmap))
    val current = bitmap.getPixel(6,12)
    assertEquals(255, Color.red(current))
    assertTrue(Color.alpha(current) in 127..128)
    assertEquals(Color.rgb(0,204,102), bitmap.getPixel(18,12))
    FileOutputStream(File(output, "alpha-probe.png")).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    bitmap.recycle()
    val completed=CountDownLatch(names.size)
    val namedFailures=mutableListOf<String>()
    val owners=names.map { Any() }
    for ((index,name) in names.withIndex()) {
      OneKeyNamedSVG.request(owners[index],context.assets,name.removeSuffix(".svg"),"rgba(255,0,0,0.5)",128,128,1f) { image ->
        try {
          if(image==null || image.width!=128 || image.height!=128) namedFailures.add(name)
          else if(representatives.contains(name)) FileOutputStream(File(output,"named-$name.png")).use { image.compress(Bitmap.CompressFormat.PNG,100,it) }
        } catch(error:Exception) { namedFailures.add("$name: ${error.message}") }
        finally { completed.countDown() }
      }
    }
    assertTrue("Full shared named decode completed",completed.await(60,TimeUnit.SECONDS))
    File(output,"named-full-coverage.json").writeText(org.json.JSONObject().put("requested",names.size).put("completed",names.size-completed.count).put("failed",org.json.JSONArray(namedFailures)).toString())
    assertEquals(namedFailures.joinToString("\n"),0,namedFailures.size)
  }

  private fun assertSemanticPixels(actual:Bitmap,expected:Bitmap,name:String,alpha:Double) {
    fun bytes(bitmap:Bitmap):ByteArray {val buffer=java.nio.ByteBuffer.allocate(bitmap.byteCount);bitmap.copyPixelsToBuffer(buffer);return buffer.array()}
    val calibration=Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888).apply{eraseColor(Color.rgb(17,34,51))};val calibrationBytes=bytes(calibration)
    val indices=listOf(17,34,51,255).map {v->calibrationBytes.indexOfFirst{(it.toInt() and 255)==v}};assertEquals(4,indices.distinct().size)
    val a=bytes(actual);val b=bytes(expected);val width=actual.width;val height=actual.height;val ai=indices[3]
    // Fixture modes are independently known from these SVG sources.
    val single=name=="DotVerSolid";val multiple=name=="ChartPositionOutline"||name=="LimitOrderOutline"
    var maximum=0;var alphaMaximum=0;var flatMaximum=0;var colorMaximum=0;var actualMass=0L;var rawMass=0L;var covered=0;var alphaError=0L
    var actualBox=intArrayOf(width,height,-1,-1);var rawBox=actualBox.clone()
    for(y in 0 until height)for(x in 0 until width){val offset=(y*width+x)*4;val aa=a[offset+ai].toInt() and 255;val ba=b[offset+ai].toInt() and 255
      actualMass+=aa;rawMass+=ba;alphaMaximum=maxOf(alphaMaximum,kotlin.math.abs(aa-ba))
      for(c in 0..3)maximum=maxOf(maximum,kotlin.math.abs((a[offset+c].toInt() and 255)-(b[offset+c].toInt() and 255)))
      if(aa>0||ba>0){covered++;alphaError+=kotlin.math.abs(aa-ba)}
      if(aa>0){actualBox=intArrayOf(minOf(actualBox[0],x),minOf(actualBox[1],y),maxOf(actualBox[2],x),maxOf(actualBox[3],y))}
      if(ba>0){rawBox=intArrayOf(minOf(rawBox[0],x),minOf(rawBox[1],y),maxOf(rawBox[2],x),maxOf(rawBox[3],y))}
      for((c,rgb) in listOf(63,111,199).withIndex())colorMaximum=maxOf(colorMaximum,kotlin.math.abs((a[offset+indices[c]].toInt() and 255)-kotlin.math.round(rgb*aa/255.0).toInt()))
      if(ba>0&&x>=2&&y>=2&&x<width-2&&y<height-2){var flat=true;for(dy in -2..2)for(dx in -2..2)for(c in 0..3)if(b[((y+dy)*width+x+dx)*4+c]!=b[offset+c])flat=false;if(flat)for(c in 0..3)flatMaximum=maxOf(flatMaximum,kotlin.math.abs((a[offset+c].toInt() and 255)-(b[offset+c].toInt() and 255)))}
    }
    if(!single&&!multiple){assertTrue("$name fixed/raster raw RGBA",maximum<=1);return}
    assertTrue("$name dynamic premultiplied RGB follows requested color",colorMaximum<=1)
    if(multiple||alpha==1.0)assertTrue("$name geometric alpha",alphaMaximum<=1)
    else if(alpha==0.0)assertEquals(0L,actualMass)
    else {assertTrue("$name flat interiors",flatMaximum<=1);assertTrue("$name bounded contour AA",alphaMaximum<=16);assertTrue(kotlin.math.abs(actualMass-rawMass)/maxOf(1.0,rawMass.toDouble())<=0.02);assertTrue(alphaError/maxOf(1.0,covered.toDouble())<=2);for(i in 0..3)assertTrue(kotlin.math.abs(actualBox[i]-rawBox[i])<=1)}
    println("ICON_SEMANTIC name=$name alpha=$alpha rawMax=$maximum alphaMax=$alphaMaximum flatMax=$flatMaximum tintMax=$colorMaximum massDelta=${actualMass-rawMass}")
  }

  fun testRenderedShapeMatchesIndependentRawDecoder() {
    val activity=instrumentation.startActivitySync(Intent(instrumentation.context,OneKeyIconTestActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    fun main(block:()->Unit)=instrumentation.runOnMainSync(block)
    val context=ThemedReactContext(BridgeReactContext(activity),activity,"RawComparison",1)
    val output=File(instrumentation.targetContext.filesDir,"shape-comparison").apply{mkdirs()}
    try {
      for(name in listOf("DotVerSolid","ChartPositionOutline","LimitOrderOutline","BotIllus","OnekeyTextIllus","BadgeVerifiedSolid")) {
        for((alpha,hex) in listOf("0" to "00","0.5" to "80","1" to "ff")) {
          lateinit var icon:HybridOneKeyIcon
          main { icon=HybridOneKeyIcon(context);activity.setContentView(FrameLayout(activity).apply{addView(icon.view,FrameLayout.LayoutParams(96,96))});icon.view.layout(0,0,96,96);icon.iconName=name;icon.resolvedColor="rgba(63,111,199,$alpha)";icon.afterUpdate() }
          var ready=false
          repeat(200){main{ready=icon.view.drawable!=null};if(!ready)SystemClock.sleep(10)}
          assertTrue(ready)
          val source=instrumentation.targetContext.assets.open("icons/$name.svg").bufferedReader().use{it.readText()}.replace("currentColor","#3f6fc7$hex")
          val expected=OneKeySVG.decode(source.byteInputStream()).render(96,96)
          val actual=Bitmap.createBitmap(96,96,Bitmap.Config.ARGB_8888)
          main { icon.view.draw(Canvas(actual));icon.dispose() }
          assertSemanticPixels(actual,expected,name,alpha.toDouble())
          File(output,"actual-$name-$alpha.png").outputStream().use{actual.compress(Bitmap.CompressFormat.PNG,100,it)}
          File(output,"raw-$name-$alpha.png").outputStream().use{expected.compress(Bitmap.CompressFormat.PNG,100,it)}
        }
      }
      fun overlap(color:String)=OneKeySVG.decode("<svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24'><rect width='16' height='24' fill='$color'/><rect x='8' width='16' height='24' fill='$color'/></svg>".byteInputStream()).render(96,96)
      val mask=overlap("rgba(0,0,0,0.5)")
      val result=OneKeyNamedSVG.RenderResult(mask,OneKeyNamedSVG.RecolorMode.MULTIPLE,"synthetic-overlap",128)
      val direct=overlap("rgba(255,0,0,0.5)");assertEquals(192,Color.alpha(direct.getPixel(48,48)))
      main {
        val probe=android.widget.ImageView(activity);probe.layout(0,0,96,96);probe.setImageBitmap(result.bitmap)
        probe.colorFilter=android.graphics.PorterDuffColorFilter(requireNotNull(result.tintColor("rgba(255,0,0,0.5)")),android.graphics.PorterDuff.Mode.SRC_IN)
        val rendered=Bitmap.createBitmap(96,96,Bitmap.Config.ARGB_8888);probe.draw(Canvas(rendered));assertEquals(192,Color.alpha(rendered.getPixel(48,48)))
        val opacityMask=OneKeySVG.decode("<svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24'><rect width='24' height='24' fill='rgba(0,0,0,1)' fill-opacity='0.4'/></svg>".byteInputStream()).render(96,96)
        val single=OneKeyNamedSVG.RenderResult(opacityMask,OneKeyNamedSVG.RecolorMode.SINGLE,"synthetic-source-opacity",255)
        probe.setImageBitmap(single.bitmap);probe.colorFilter=android.graphics.PorterDuffColorFilter(requireNotNull(single.tintColor("rgba(255,0,0,0.5)")),android.graphics.PorterDuff.Mode.SRC_IN)
        rendered.eraseColor(0);probe.draw(Canvas(rendered));assertEquals(51,Color.alpha(rendered.getPixel(48,48)))
      }
    } finally { main{activity.finish()} }
  }

  fun testShapeTintIsImmediateAndPendingAlphaRetainsPixels() {
    val activity=instrumentation.startActivitySync(Intent(instrumentation.context,OneKeyIconTestActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    lateinit var icon:HybridOneKeyIcon
    lateinit var other:HybridOneKeyIcon
    fun main(block:()->Unit)=instrumentation.runOnMainSync(block)
    fun awaitImage() { repeat(200) { var ready=false;main { ready=icon.view.drawable!=null };if(ready)return;SystemClock.sleep(10) };fail("Missing current shape") }
    val metadataLock=OneKeyNamedSVG::class.java.getDeclaredField("manifestLock").apply{isAccessible=true}.get(OneKeyNamedSVG)
    val pendingField=OneKeyNamedSVG::class.java.getDeclaredField("pending").apply{isAccessible=true}
    try {
      main {
        val context=ThemedReactContext(BridgeReactContext(activity),activity,"ShapeTint",1)
        icon=HybridOneKeyIcon(context);other=HybridOneKeyIcon(context)
        val container=FrameLayout(activity);container.addView(icon.view,FrameLayout.LayoutParams(96,96));container.addView(other.view,FrameLayout.LayoutParams(96,96).apply{leftMargin=120})
        activity.setContentView(container);icon.view.layout(0,0,96,96);other.view.layout(120,0,216,96)
        icon.iconName="DotVerSolid";icon.resolvedColor="rgba(255,0,0,1)";icon.afterUpdate()

      }
      awaitImage()
      main{other.iconName="DotVerSolid";other.resolvedColor="rgba(0,0,255,1)";other.afterUpdate()}
      repeat(100){var ready=false;main{ready=other.view.drawable!=null};if(!ready)SystemClock.sleep(10)}
      synchronized(metadataLock) {
        main {
          val mask=(icon.view.drawable as BitmapDrawable).bitmap
          icon.view.visibility=View.INVISIBLE;assertSame(mask,(icon.view.drawable as BitmapDrawable).bitmap)
          icon.view.visibility=View.VISIBLE;assertSame(mask,(icon.view.drawable as BitmapDrawable).bitmap)
          assertSame(mask,(other.view.drawable as BitmapDrawable).bitmap)
          icon.resolvedColor="rgba(0,0,255,1)";icon.afterUpdate()
          icon.resolvedColor="rgba(0,255,0,0.5)";icon.afterUpdate()
          assertSame(mask,(icon.view.drawable as BitmapDrawable).bitmap)
          val bitmap=Bitmap.createBitmap(96,96,Bitmap.Config.ARGB_8888);icon.view.draw(Canvas(bitmap))
          assertEquals(Color.argb(128,0,255,0),bitmap.getPixel(48,48))
          bitmap.eraseColor(0);other.view.draw(Canvas(bitmap));assertEquals(Color.BLUE,bitmap.getPixel(48,48))
          assertEquals(Color.BLACK,mask.getPixel(48,48))
          assertTrue((pendingField.get(OneKeyNamedSVG) as Map<*,*>).isEmpty())
          assertEquals(0,OneKeyNamedSVG::class.java.getDeclaredField("activeWorkers").apply{isAccessible=true}.getInt(OneKeyNamedSVG))
          icon.iconName="ChartPositionOutline";icon.resolvedColor="rgba(255,0,0,1)";icon.afterUpdate();assertNull(icon.view.drawable)
        }
      }
      awaitImage()
      synchronized(metadataLock) {
        main {
          val mask=(icon.view.drawable as BitmapDrawable).bitmap
          icon.resolvedColor="rgba(0,0,255,1)";icon.afterUpdate();assertSame(mask,(icon.view.drawable as BitmapDrawable).bitmap)
          icon.resolvedColor="rgba(0,255,0,0.5)";icon.afterUpdate();assertSame(mask,(icon.view.drawable as BitmapDrawable).bitmap)
          icon.resolvedColor="invalid";icon.afterUpdate();assertNull(icon.view.drawable)
        }
      }
      SystemClock.sleep(100);main{assertNull(icon.view.drawable);icon.iconName="BotIllus";icon.resolvedColor="rgba(255,0,0,1)";icon.afterUpdate()}
      awaitImage()
      main {
        val image=(icon.view.drawable as BitmapDrawable).bitmap
        icon.resolvedColor="rgba(0,0,255,0)";icon.afterUpdate();assertSame(image,(icon.view.drawable as BitmapDrawable).bitmap)
        icon.prepareForRecycle();assertNull(icon.view.drawable)
      }
    } finally { main { icon.dispose();other.dispose();activity.finish() } }
  }

  fun testDiagnosticPendingDetachThenReattach() {
    val activity=instrumentation.startActivitySync(Intent(instrumentation.context,OneKeyIconTestActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    lateinit var icon: HybridOneKeyIcon
    lateinit var container: FrameLayout
    fun main(action:()->Unit) { instrumentation.runOnMainSync(action) }
    val metadataLock=OneKeyNamedSVG::class.java.getDeclaredField("manifestLock").apply { isAccessible=true }.get(OneKeyNamedSVG)
    synchronized(metadataLock) {
      main {
        icon=HybridOneKeyIcon(ThemedReactContext(BridgeReactContext(activity),activity,"DetachDiagnostic",1))
        container=FrameLayout(activity);container.addView(icon.view,FrameLayout.LayoutParams(64,64));activity.setContentView(container)
        icon.view.layout(0,0,64,64)
        icon.iconName="DotVerSolid";icon.resolvedColor="rgba(0,0,255,1)";icon.afterUpdate()
        container.removeView(icon.view)
      }
    }
    // Let the in-flight completion run while actually detached, in a later main turn.
    SystemClock.sleep(500);instrumentation.waitForIdleSync()
    main { assertNull(icon.view.drawable);container.addView(icon.view,FrameLayout.LayoutParams(64,64));icon.view.layout(0,0,64,64) }
    var painted=false
    repeat(100) { main { painted=icon.view.drawable!=null };if(!painted)SystemClock.sleep(20) }
    println("ICON_BASELINE_DETACH_DIAGNOSTIC reattachedImage=$painted")
    main { icon.dispose();activity.finish() }
    assertTrue("Same icon must recover after detached pending completion",painted)
  }


  fun testNativeViewReuseAndBoundedResourceCache() {
    val activity = instrumentation.startActivitySync(Intent(instrumentation.context, OneKeyIconTestActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    lateinit var icon: HybridOneKeyIcon
    lateinit var container: FrameLayout
    fun main(block: () -> Unit) = instrumentation.runOnMainSync(block)
    fun awaitImage() {
      val deadline = SystemClock.uptimeMillis() + 5000
      while (SystemClock.uptimeMillis() < deadline) {
        var ready = false
        main { ready = icon.view.drawable != null }
        if (ready) return
        SystemClock.sleep(10)
      }
      fail("Current named icon was not displayed")
    }
    try {
      main {
        icon = HybridOneKeyIcon(ThemedReactContext(BridgeReactContext(activity), activity, "IconTest", 1))
        container = FrameLayout(activity)
        container.addView(icon.view,FrameLayout.LayoutParams(64,64))
        activity.setContentView(container)
        icon.view.layout(0,0,64,64)
        repeat(100) { index ->
          icon.iconName = if (index % 2 == 0) "BotIllus" else "DotVerSolid"
          icon.resolvedColor = "rgba(255,0,0,1)"
          icon.afterUpdate()
        }
        icon.iconName = "DotVerSolid"
        icon.resolvedColor = "rgba(0,0,255,0.5)"
        icon.afterUpdate()
      }
      awaitImage()
      main {
        val bitmap = Bitmap.createBitmap(icon.view.width, icon.view.height, Bitmap.Config.ARGB_8888)
        icon.view.draw(Canvas(bitmap))
        var blue = 0; var red = 0
        for (y in 0 until bitmap.height) for (x in 0 until bitmap.width) {
          val pixel = bitmap.getPixel(x,y)
          if (Color.alpha(pixel)>0 && Color.blue(pixel)>200) blue++
          if (Color.alpha(pixel)>0 && Color.red(pixel)>200) red++
        }
        assertTrue(blue > 0); assertEquals(0,red)
        val output = File(instrumentation.targetContext.filesDir,"icon-probe").apply { mkdirs() }
        FileOutputStream(File(output,"native-view-blue.png")).use { bitmap.compress(Bitmap.CompressFormat.PNG,100,it) }
        bitmap.recycle()
        icon.iconName = "MissingIcon"
        icon.afterUpdate()
        assertNull(icon.view.drawable)
        icon.iconName = "BotIllus"
        icon.afterUpdate()
        container.removeView(icon.view)
        assertNull(icon.view.drawable)
        container.addView(icon.view,FrameLayout.LayoutParams(64,64))
      }
      awaitImage()
      main {
        icon.view.layoutParams = FrameLayout.LayoutParams(96,64)
        icon.view.layout(0,0,96,64)
      }
      awaitImage()
      main {
        assertEquals(96,(icon.view.drawable as BitmapDrawable).bitmap.width)
        assertEquals(64,(icon.view.drawable as BitmapDrawable).bitmap.height)
        icon.prepareForRecycle()
        assertNull(icon.view.drawable)
      }
      val assets = instrumentation.targetContext.assets
      var firstSizeBitmap: Bitmap? = null
      var lastSizeBitmap: Bitmap? = null
      for (width in 24 until 174) {
        val latch = CountDownLatch(1)
        val output = java.util.concurrent.atomic.AtomicReference<Bitmap>()
        OneKeyNamedSVG.request(Any(), assets, "DotVerSolid", "rgba(0,0,0,1)",width,24,1f) { output.set(it); latch.countDown() }
        assertTrue(latch.await(5,TimeUnit.SECONDS))
        assertNotNull(output.get())
        if (width == 24) firstSizeBitmap = output.get()
        lastSizeBitmap = output.get()
      }
      val cacheField = OneKeyNamedSVG::class.java.getDeclaredField("cache").apply { isAccessible = true }
      @Suppress("UNCHECKED_CAST") val cache = cacheField.get(OneKeyNamedSVG) as Map<String,OneKeyNamedSVG.RenderResult>
      assertEquals(128,cache.size)
      assertFalse(cache.values.any { it.bitmap === firstSizeBitmap })
      assertTrue(cache.values.any { it.bitmap === lastSizeBitmap })
      assertTrue(cache.values.sumOf { it.bitmap.allocationByteCount.toLong() } <= 8388608)
      val executorField = OneKeyNamedSVG::class.java.getDeclaredField("workers").apply { isAccessible = true }
      val executor = executorField.get(OneKeyNamedSVG) as ExecutorService
      val started = CountDownLatch(2)
      val unblock = CountDownLatch(1)
      repeat(2) { executor.execute { started.countDown(); unblock.await(5,TimeUnit.SECONDS) } }
      assertTrue(started.await(5,TimeUnit.SECONDS))
      val owner = Any()
      val pendingField = OneKeyNamedSVG::class.java.getDeclaredField("pending").apply { isAccessible = true }
      try {
        repeat(100) { OneKeyNamedSVG.request(owner, assets, "DotVerSolid", "rgba(0,0,255,1)",24,24,1f) {} }
        assertEquals(1,(pendingField.get(OneKeyNamedSVG) as Map<*,*>).size)
        val queued = (executor as ThreadPoolExecutor).queue.size
        assertTrue(queued <= 2)
        OneKeyNamedSVG.cancel(owner)
        assertEquals(0,(pendingField.get(OneKeyNamedSVG) as Map<*,*>).size)
        assertEquals(queued,executor.queue.size)
      } finally { unblock.countDown() }
      val mixedDone = CountDownLatch(1)
      var mixed: Bitmap? = null
      OneKeyNamedSVG.request(Any(), assets, "OnekeyTextIllus", "rgba(255,0,0,0)",128,128,1f) { mixed = it; mixedDone.countDown() }
      assertTrue(mixedDone.await(5,TimeUnit.SECONDS))
      assertNotNull(mixed)
      var green = 0; var red = 0
      for (y in 0 until 128) for (x in 0 until 128) {
        val pixel = mixed!!.getPixel(x,y)
        if (Color.alpha(pixel)>0 && Color.green(pixel)>Color.red(pixel)) green++
        if (Color.alpha(pixel)>0 && Color.red(pixel)>Color.green(pixel)) red++
      }
      assertTrue(green>0); assertEquals(0,red)
    } finally { main { icon.dispose(); activity.finish() } }
  }
}
