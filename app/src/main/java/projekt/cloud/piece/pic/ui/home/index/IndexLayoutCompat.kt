package projekt.cloud.piece.pic.ui.home.index

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Rect
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.ObservableArrayMap
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.appbar.MaterialToolbar
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.collections.CollectionsResponseBody.Data.Collection.Comic
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter.BaseRecyclerViewAdapterUtil.adapterInterface
import projekt.cloud.piece.pic.base.SnackLayoutCompat
import projekt.cloud.piece.pic.databinding.FragmentIndexBinding
import projekt.cloud.piece.pic.util.AdapterInterface
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED

abstract class IndexLayoutCompat private constructor(
    protected val binding: FragmentIndexBinding
): SnackLayoutCompat(), AdapterInterface {
    
    companion object IndexLayoutCompatUtil {
        @JvmStatic
        fun FragmentIndexBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> IndexLayoutCompatImpl(this)
            MEDIUM -> IndexLayoutCompatW600dpImpl(this)
            EXPANDED -> IndexLayoutCompatW1240dpImpl(this)
        }
    }
    
    private val coordinatorLayout: CoordinatorLayout
        get() = binding.coordinatorLayout
    
    protected val toolbar: MaterialToolbar
        get() = binding.materialToolbar!!
    
    private val recyclerViewA: RecyclerView
        get() = binding.recyclerViewRecommendA
    
    private val recyclerViewB: RecyclerView
        get() = binding.recyclerViewRecommendB
    
    protected lateinit var navController: NavController
        private set
    
    fun setNavController(navController: NavController) {
        this.navController = navController
    }
    
    fun setupRecyclerViews(resources: Resources, comicListA: List<Comic>, comicListB: List<Comic>, coverMap: ObservableArrayMap<String, Bitmap?>) {
        val recyclerViewListSpacingVertical = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
        
        recyclerViewA.adapter = RecyclerViewAdapter(comicListA, coverMap)
        recyclerViewA.addItemDecoration(object: ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.top = recyclerViewListSpacingVertical
                outRect.bottom = recyclerViewListSpacingVertical
            }
        })
        
        recyclerViewB.adapter = RecyclerViewAdapter(comicListB, coverMap)
        recyclerViewB.addItemDecoration(object: ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.top = recyclerViewListSpacingVertical
                outRect.bottom = recyclerViewListSpacingVertical
            }
        })
    }
    
    override fun notifyClear() {
        recyclerViewA.adapterInterface.notifyClear()
        recyclerViewB.adapterInterface.notifyClear()
    }
    
    override fun notifyUpdate() {
        recyclerViewA.adapterInterface.notifyUpdate()
        recyclerViewB.adapterInterface.notifyUpdate()
    }
    
    open fun setupActionBar(fragment: Fragment) = Unit
    
    override val snackContainer: View
        get() = coordinatorLayout
    
    private class IndexLayoutCompatImpl(binding: FragmentIndexBinding): IndexLayoutCompat(binding) {
    
        override fun setupActionBar(fragment: Fragment) {
            fragment.setSupportActionBar(toolbar)
            // Index -> NavHostFragment -> Home
            val drawerLayout = fragment.requireParentFragment()
                .requireParentFragment()
                .requireView()
                .findViewById<DrawerLayout>(R.id.drawer_layout)
            // Just open drawer, allow NavigationView switching fragments,
            // all fragments in NavController is singleTop mode
            toolbar.setNavigationOnClickListener {
                if (!drawerLayout.isOpen) {
                    drawerLayout.open()
                }
            }
        }
        
    }
    
    private class IndexLayoutCompatW600dpImpl(binding: FragmentIndexBinding): IndexLayoutCompat(binding)
    
    private class IndexLayoutCompatW1240dpImpl(binding: FragmentIndexBinding): IndexLayoutCompat(binding)
    
}