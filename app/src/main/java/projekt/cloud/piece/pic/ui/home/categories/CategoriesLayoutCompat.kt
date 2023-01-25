package projekt.cloud.piece.pic.ui.home.categories

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
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
import projekt.cloud.piece.pic.ui.empty.EmptyDirections
import projekt.cloud.piece.pic.ui.home.HomeDirections
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
    
    protected lateinit var navController: NavController
        private set
    open fun setNavController(navController: NavController) {
        this.navController = navController
    }
    
    protected val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    open fun setupActionBar(fragment: Fragment, drawerHostFragment: Fragment) = Unit
    
    open fun setupRecyclerView(categoryList: List<Category>, fragment: Fragment, resources: Resources) {
        recyclerView.adapter = RecyclerViewAdapter(categoryList, fragment, onRecyclerClick)
    }
    
    protected abstract val onRecyclerClick: (String) -> Unit
    
    override fun notifyClear() = Unit
    
    override fun notifyUpdate() {
        recyclerView.adapterInterface.notifyUpdate()
    }
    
    private class CategoriesLayoutCompatImpl(binding: FragmentCategoriesBinding): CategoriesLayoutCompat(binding) {
        
        companion object {
            const val GRID_SPAN = 2
        }
    
        override val onRecyclerClick: (String) -> Unit
            get() = {
                navController.navigate(HomeDirections.toCategory(it))
            }
        
        private val toolbar: MaterialToolbar
            get() = binding.materialToolbar!!
    
        override fun setupActionBar(fragment: Fragment, drawerHostFragment: Fragment) {
            drawerHostFragment.setSupportActionBar(toolbar)
            val drawerLayout = drawerHostFragment.requireView().findViewById<DrawerLayout>(R.id.drawer_layout)
            toolbar.setNavigationOnClickListener {
                if (!drawerLayout.isOpen) {
                    drawerLayout.open()
                }
            }
        }
    
        override fun setupRecyclerView(categoryList: List<Category>, fragment: Fragment, resources: Resources) {
            (recyclerView.layoutManager as GridLayoutManager).spanCount = GRID_SPAN
            super.setupRecyclerView(categoryList, fragment, resources)
            val verticalMargin = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            val horizontalMarginInner = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
            recyclerView.addItemDecoration(
                object: ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        outRect.bottom = verticalMargin
                        outRect.right = horizontalMarginInner
                    }
                }
            )
        }
        
    }
    
    private class CategoriesLayoutCompatW600dpImpl(binding: FragmentCategoriesBinding): CategoriesLayoutCompat(binding) {
    
        private companion object {
            const val GRID_SPAN = 4
        }
    
        override val onRecyclerClick: (String) -> Unit
            get() = {
                navController.navigate(HomeDirections.toCategory(it))
            }
        
        override fun setupRecyclerView(categoryList: List<Category>, fragment: Fragment, resources: Resources) {
            (recyclerView.layoutManager as GridLayoutManager).spanCount = GRID_SPAN
            super.setupRecyclerView(categoryList, fragment, resources)
            val verticalMargin = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            val horizontalMarginInner = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
            recyclerView.addItemDecoration(
                object: ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        outRect.bottom = verticalMargin
                        outRect.right = horizontalMarginInner
                    }
                }
            )
        }
        
    }
    
    private class CategoriesLayoutCompatW1240dpImpl(binding: FragmentCategoriesBinding): CategoriesLayoutCompat(binding) {
    
        private val fragmentContainerView: FragmentContainerView
            get() = binding.fragmentContainerView!!
        
        override val onRecyclerClick: (String) -> Unit
            get() = {
                navController.navigate(
                    when (navController.currentDestination?.id) {
                        R.id.category -> {
                            /**
                             * // Cannot be generated by `Android Studio Giraffe | 2022.3.1 Canary 1` by unknown reason
                             * CategoryDirections.toCategory(it)
                             **/
                            object: NavDirections {
                                override val actionId: Int
                                    get() = R.id.to_category
                                override val arguments: Bundle
                                    get() = bundleOf(
                                        binding.coordinatorLayout.context.getString(R.string.category_title) to it
                                    )
                            }
                        }
                        else -> EmptyDirections.toCategory(it)
                    }
                )
            }
    
        override fun setNavController(navController: NavController) {
            super.setNavController(
                fragmentContainerView.getFragment<NavHostFragment>().navController
            )
        }
    
        override fun setupRecyclerView(categoryList: List<Category>, fragment: Fragment, resources: Resources) {
            super.setupRecyclerView(categoryList, fragment, resources)
            val verticalMargin = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            recyclerView.addItemDecoration(
                object: ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        outRect.bottom = verticalMargin
                    }
                }
            )
        }
        
    }
    
}