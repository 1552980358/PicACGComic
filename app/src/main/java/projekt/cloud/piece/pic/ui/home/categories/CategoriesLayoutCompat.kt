package projekt.cloud.piece.pic.ui.home.categories

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.State
import com.google.android.material.appbar.MaterialToolbar
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.comics.categories.CategoriesResponseBody.Category
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter.BaseRecyclerViewAdapterUtil.adapterInterface
import projekt.cloud.piece.pic.base.SnackLayoutCompat
import projekt.cloud.piece.pic.databinding.FragmentCategoriesBinding
import projekt.cloud.piece.pic.util.AdapterInterface
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM

abstract class CategoriesLayoutCompat private constructor(
    protected val binding: FragmentCategoriesBinding
): SnackLayoutCompat(), AdapterInterface {
    
    companion object CategoriesLayoutCompatUtil {
        @JvmStatic
        fun FragmentCategoriesBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> CategoriesLayoutCompatImpl(this)
            MEDIUM -> CategoriesLayoutCompatW600dpImpl(this)
            EXPANDED -> CategoriesLayoutCompatW1240dpImpl(this)
        }
    }
    
    override val snackContainer: View
        get() = binding.coordinatorLayout
    
    private lateinit var navController: NavController
    fun setNavController(navController: NavController) {
        this.navController = navController
    }
    
    protected val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    protected abstract val recyclerViewGrid: Int
    
    open fun setupActionBar(fragment: Fragment, drawerHostFragment: Fragment) = Unit
    
    open fun setupRecyclerView(categoryList: List<Category>, resources: Resources) {
        (recyclerView.layoutManager as GridLayoutManager).spanCount = recyclerViewGrid
        recyclerView.adapter = RecyclerViewAdapter(categoryList) { title -> }
    }
    
    override fun notifyClear() = Unit
    
    override fun notifyUpdate() {
        recyclerView.adapterInterface.notifyUpdate()
    }
    
    private class CategoriesLayoutCompatImpl(binding: FragmentCategoriesBinding): CategoriesLayoutCompat(binding) {
        
        companion object {
            const val GRID_SPAN = 2
        }
    
        private val toolbar: MaterialToolbar
            get() = binding.materialToolbar!!
        
        override val recyclerViewGrid: Int
            get() = GRID_SPAN
    
        override fun setupActionBar(fragment: Fragment, drawerHostFragment: Fragment) {
            drawerHostFragment.setSupportActionBar(toolbar)
            val drawerLayout = drawerHostFragment.requireView().findViewById<DrawerLayout>(R.id.drawer_layout)
            toolbar.setNavigationOnClickListener {
                if (!drawerLayout.isOpen) {
                    drawerLayout.open()
                }
            }
        }
    
        override fun setupRecyclerView(categoryList: List<Category>, resources: Resources) {
            super.setupRecyclerView(categoryList, resources)
            
            val verticalMargin = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            val horizontalMarginInner = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
            
            recyclerView.addItemDecoration(
                object: ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        outRect.bottom = verticalMargin
                        if ((view.layoutParams as GridLayoutManager.LayoutParams).spanIndex == 0) {
                            outRect.right = horizontalMarginInner
                        }
                    }
                }
            )
        }
        
    }
    
    private class CategoriesLayoutCompatW600dpImpl(binding: FragmentCategoriesBinding): CategoriesLayoutCompat(binding) {
    
        private companion object {
            const val GRID_SPAN = 4
        }
    
        override val recyclerViewGrid: Int
            get() = GRID_SPAN
    
        override fun setupRecyclerView(categoryList: List<Category>, resources: Resources) {
            super.setupRecyclerView(categoryList, resources)
        
            val verticalMargin = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            val horizontalMarginInner = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
        
            recyclerView.addItemDecoration(
                object: ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        outRect.bottom = verticalMargin
                        if ((view.layoutParams as GridLayoutManager.LayoutParams).spanIndex in (0 until GRID_SPAN - 1)) {
                            outRect.right = horizontalMarginInner
                        }
                    }
                }
            )
        }
        
    }
    
    private class CategoriesLayoutCompatW1240dpImpl(binding: FragmentCategoriesBinding): CategoriesLayoutCompat(binding) {
    
        private companion object {
            const val GRID_SPAN = 6
        }
    
        override val recyclerViewGrid: Int
            get() = GRID_SPAN
        
        override fun setupRecyclerView(categoryList: List<Category>, resources: Resources) {
            super.setupRecyclerView(categoryList, resources)
        
            val verticalMargin = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            val horizontalMarginInner = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
        
            recyclerView.addItemDecoration(
                object: ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        outRect.bottom = verticalMargin
                        if ((view.layoutParams as GridLayoutManager.LayoutParams).spanIndex in (0 until GRID_SPAN - 1)) {
                            outRect.right = horizontalMarginInner
                        }
                    }
                }
            )
        }
        
    }
    
}