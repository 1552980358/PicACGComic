package projekt.cloud.piece.pic.ui.category

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import projekt.cloud.piece.pic.api.comics.ComicsResponseBody.Comic
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter.BaseRecyclerViewAdapterUtil.adapterInterface
import projekt.cloud.piece.pic.base.SnackLayoutCompat
import projekt.cloud.piece.pic.databinding.FragmentCategoryBinding
import projekt.cloud.piece.pic.ui.home.Home
import projekt.cloud.piece.pic.util.AdapterInterface
import projekt.cloud.piece.pic.util.FragmentUtil.findParentAs
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM

abstract class CategoryLayoutCompat(
    protected val binding: FragmentCategoryBinding
): SnackLayoutCompat(), AdapterInterface {
    
    companion object CategoryLayoutCompatUtil {
        @JvmStatic
        fun FragmentCategoryBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> CategoryLayoutCompatImpl(this)
            MEDIUM -> CategoryLayoutCompatW600dpImpl(this)
            EXPANDED -> CategoryLayoutCompatW1240dpImpl(this)
        }
    }
    
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    override val snackContainer: View
        get() = binding.coordinatorLayout
    
    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    
    private lateinit var navController: NavController
    open fun setNavController(fragment: Fragment) {
        this.navController = fragment.findNavController()
    }
    
    open fun setupActionBar(fragment: Fragment) {
        fragment.setSupportActionBar(toolbar)
    }
    
    fun setupRecyclerView(fragment: Fragment, comicList: List<Comic>) {
        recyclerView.adapter = RecyclerViewAdapter(comicList, fragment) { id, title, appCompatImageView ->
        }
    }
    
    override fun notifyClear() {
        recyclerView.adapterInterface.notifyClear()
    }
    
    override fun notifyUpdate() {
        recyclerView.adapterInterface.notifyUpdate()
    }
    
    private class CategoryLayoutCompatImpl(binding: FragmentCategoryBinding): CategoryLayoutCompat(binding) {
    
    }
    
    private class CategoryLayoutCompatW600dpImpl(binding: FragmentCategoryBinding): CategoryLayoutCompat(binding)
    
    private class CategoryLayoutCompatW1240dpImpl(binding: FragmentCategoryBinding): CategoryLayoutCompat(binding) {
    
        override fun setNavController(fragment: Fragment) {
            super.setNavController(fragment.findParentAs<Home>())
        }
        
    }
    
}