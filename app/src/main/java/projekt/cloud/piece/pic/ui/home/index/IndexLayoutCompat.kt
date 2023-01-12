package projekt.cloud.piece.pic.ui.home.index

import android.content.res.Resources
import android.graphics.Rect
import android.transition.TransitionManager
import android.view.View
import android.view.View.GONE
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.transition.platform.MaterialFadeThrough
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.collections.CollectionsResponseBody
import projekt.cloud.piece.pic.api.comics.random.RandomResponseBody
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter.BaseRecyclerViewAdapterUtil.adapterInterface
import projekt.cloud.piece.pic.base.SnackLayoutCompat
import projekt.cloud.piece.pic.databinding.FragmentIndexBinding
import projekt.cloud.piece.pic.ui.home.HomeDirections
import projekt.cloud.piece.pic.util.AdapterInterface
import projekt.cloud.piece.pic.util.BitmapBundle.Companion.toNavArg
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
    protected val recyclerViewA: RecyclerView
        get() = binding.recyclerViewRecommendA
    protected val recyclerViewB: RecyclerView
        get() = binding.recyclerViewRecommendB
    protected val random: RecyclerView
        get() = binding.recyclerViewRandom
    
    private val container: ConstraintLayout
        get() = binding.constraintLayoutContainer
    
    private val content: ConstraintLayout
        get() = binding.constraintLayoutContent
    
    private val onClick: (String, String, AppCompatImageView) -> Unit = { id, title, appCompatImageView ->
        navController.navigate(
            HomeDirections.toComic(id, title, appCompatImageView.drawable?.toBitmapOrNull()?.toNavArg())
        )
    }
    
    protected lateinit var navController: NavController
        private set
    
    protected abstract val progressIndicator: BaseProgressIndicator<*>
    
    fun setNavController(navController: NavController) {
        this.navController = navController
    }
    
    open fun setupCollections(resources: Resources,
                              comicListA: List<CollectionsResponseBody.Comic>,
                              comicListB: List<CollectionsResponseBody.Comic>,
                              fragment: Fragment) {
        recyclerViewA.adapter = CollectionsRecyclerViewAdapter(comicListA, fragment, onClick)
        recyclerViewB.adapter = CollectionsRecyclerViewAdapter(comicListB, fragment, onClick)
    }
    
    open fun setupRandom(resources: Resources, comicList: List<RandomResponseBody.Comic>, fragment: Fragment) {
        random.adapter = RandomRecyclerViewAdapter(comicList, fragment, onClick)
    }
    
    open fun completeLoading() {
        progressIndicator.setVisibilityAfterHide(GONE)
        TransitionManager.beginDelayedTransition(
            container,
            MaterialFadeThrough().setDuration(container.resources.getInteger(R.integer.animation_duration).toLong())
        )
        progressIndicator.hide()
        content.isVisible = true
    }
    
    override fun notifyClear() {
        recyclerViewA.adapterInterface.notifyClear()
        recyclerViewB.adapterInterface.notifyClear()
        random.adapterInterface.notifyClear()
    }
    
    override fun notifyUpdate() {
        recyclerViewA.adapterInterface.notifyUpdate()
        recyclerViewB.adapterInterface.notifyUpdate()
        random.adapterInterface.notifyUpdate()
    }
    
    open fun setupActionBar(fragment: Fragment) = Unit
    
    override val snackContainer: View
        get() = coordinatorLayout
    
    private class IndexLayoutCompatImpl(binding: FragmentIndexBinding): IndexLayoutCompat(binding) {
    
        override val progressIndicator: BaseProgressIndicator<*>
            get() = binding.linearProgressIndicator as BaseProgressIndicator<*>
    
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
        
        override fun setupCollections(resources: Resources, comicListA: List<CollectionsResponseBody.Comic>, comicListB: List<CollectionsResponseBody.Comic>, fragment: Fragment) {
            super.setupCollections(resources, comicListA, comicListB, fragment)
    
            val recyclerViewListSpacingVertical = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            val itemDecoration = object: ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.top = recyclerViewListSpacingVertical
                    outRect.bottom = recyclerViewListSpacingVertical
                }
            }
            recyclerViewA.addItemDecoration(itemDecoration)
            recyclerViewB.addItemDecoration(itemDecoration)
        }
    
        override fun setupRandom(resources: Resources, comicList: List<RandomResponseBody.Comic>, fragment: Fragment) {
            super.setupRandom(resources, comicList, fragment)
            val recyclerViewListSpacingVertical = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            random.addItemDecoration(
                object: ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        outRect.bottom = recyclerViewListSpacingVertical
                    }
                }
            )
        }
        
    }
    
    private class IndexLayoutCompatW600dpImpl(binding: FragmentIndexBinding): IndexLayoutCompat(binding) {
        
        private companion object {
            const val GRID_SPAN = 2
        }
        
        override val progressIndicator: BaseProgressIndicator<*>
            get() = binding.circularProgressIndicator as BaseProgressIndicator<*>
    
        override fun setupCollections(resources: Resources,
                                      comicListA: List<CollectionsResponseBody.Comic>,
                                      comicListB: List<CollectionsResponseBody.Comic>,
                                      fragment: Fragment) {
            super.setupCollections(resources, comicListA, comicListB, fragment)
            
            (recyclerViewA.layoutManager as GridLayoutManager).spanCount = GRID_SPAN
            (recyclerViewB.layoutManager as GridLayoutManager).spanCount = GRID_SPAN
    
            val recyclerViewListSpacingVertical = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            val recyclerViewListSpacingHorizontal = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
            val itemDecoration = object: ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.bottom = recyclerViewListSpacingVertical
                    outRect.right = recyclerViewListSpacingHorizontal
                }
            }
            recyclerViewA.addItemDecoration(itemDecoration)
            recyclerViewB.addItemDecoration(itemDecoration)
        }
    
        override fun setupRandom(resources: Resources, comicList: List<RandomResponseBody.Comic>, fragment: Fragment) {
            super.setupRandom(resources, comicList, fragment)
            (random.layoutManager as GridLayoutManager).spanCount = GRID_SPAN
            val recyclerViewListSpacingVertical = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            val recyclerViewListSpacingHorizontal = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
            random.addItemDecoration(
                object: ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        outRect.bottom = recyclerViewListSpacingVertical
                        outRect.right = recyclerViewListSpacingHorizontal
                    }
                }
            )
        }
        
    }
    
    private class IndexLayoutCompatW1240dpImpl(binding: FragmentIndexBinding): IndexLayoutCompat(binding) {
        
        private companion object {
            const val GRID_SPAN = 2
        }
        
        override val progressIndicator: BaseProgressIndicator<*>
            get() = binding.circularProgressIndicator as BaseProgressIndicator<*>
    
        override fun setupCollections(resources: Resources,
                                      comicListA: List<CollectionsResponseBody.Comic>,
                                      comicListB: List<CollectionsResponseBody.Comic>,
                                      fragment: Fragment) {
            super.setupCollections(resources, comicListA, comicListB, fragment)
            (recyclerViewA.layoutManager as? LinearLayoutManager)?.orientation = HORIZONTAL
            (recyclerViewB.layoutManager as? LinearLayoutManager)?.orientation = HORIZONTAL
            
            val recyclerViewListSpacingHorizontalEdge = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_16)
            val recyclerViewListSpacingHorizontal = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
            recyclerViewA.addItemDecoration(
                object: ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        when (parent.getChildAdapterPosition(view)) {
                            0 -> {
                                outRect.left = recyclerViewListSpacingHorizontalEdge
                                outRect.right = recyclerViewListSpacingHorizontal
                            }
                            comicListA.lastIndex -> {
                                outRect.left = recyclerViewListSpacingHorizontal
                                outRect.right = recyclerViewListSpacingHorizontalEdge
                            }
                            else -> {
                                outRect.left = recyclerViewListSpacingHorizontal
                                outRect.right = recyclerViewListSpacingHorizontal
                            }
                        }
                    }
                }
            )
            recyclerViewB.addItemDecoration(
                object: ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        when (parent.getChildAdapterPosition(view)) {
                            0 -> {
                                outRect.left = recyclerViewListSpacingHorizontalEdge
                                outRect.right = recyclerViewListSpacingHorizontal
                            }
                            comicListB.lastIndex -> {
                                outRect.left = recyclerViewListSpacingHorizontal
                                outRect.right = recyclerViewListSpacingHorizontalEdge
                            }
                            else -> {
                                outRect.left = recyclerViewListSpacingHorizontal
                                outRect.right = recyclerViewListSpacingHorizontal
                            }
                        }
                    }
                }
            )
        }
    
        override fun setupRandom(resources: Resources, comicList: List<RandomResponseBody.Comic>, fragment: Fragment) {
            super.setupRandom(resources, comicList, fragment)
            with(random.layoutManager as GridLayoutManager) {
                orientation = HORIZONTAL
                spanCount = GRID_SPAN
            }
            
            val recyclerViewListSpacingHorizontalEdge = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_16)
            val recyclerViewListSpacingHorizontal = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
            val recyclerViewListSpacingBottom = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            
            random.addItemDecoration(
                object: ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        
                        val isEven = (comicList.size % 2) == 0
                        val left: Int
                        val right: Int
                        
                        val index = parent.indexOfChild(view)
                        when {
                            index in (0 until 2) -> {
                                left = recyclerViewListSpacingHorizontalEdge
                                right = recyclerViewListSpacingHorizontal
                            }
                            index == comicList.lastIndex -> {
                                left = recyclerViewListSpacingHorizontal
                                right = recyclerViewListSpacingHorizontalEdge
                            }
                            index == comicList.lastIndex - 1 && isEven -> {
                                left = recyclerViewListSpacingHorizontal
                                right = recyclerViewListSpacingHorizontalEdge
                            }
                            else -> {
                                left = recyclerViewListSpacingHorizontal
                                right = recyclerViewListSpacingHorizontal
                            }
                        }
                        outRect.left = left
                        outRect.right = right
                        outRect.bottom = recyclerViewListSpacingBottom
                    }
                }
            )
        }
        
    }
    
}