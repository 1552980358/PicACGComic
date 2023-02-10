package projekt.cloud.piece.pic.ui.comic

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton.OnChangedCallback
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.transition.platform.MaterialSharedAxis
import kotlin.math.min
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.databinding.ComicNavigationMenuItemActionBinding
import projekt.cloud.piece.pic.databinding.FragmentComicBinding
import projekt.cloud.piece.pic.databinding.NavHeaderComicBinding
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED

abstract class ComicLayoutCompat private constructor(protected val binding: FragmentComicBinding) {
    
    companion object ComicLayoutCompatUtil {
        @JvmStatic
        fun FragmentComicBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> ComicLayoutCompatImpl(this)
            MEDIUM -> ComicLayoutCompatW600dpImpl(this)
            EXPANDED -> ComicLayoutCompatW1240dpImpl(this)
        }
    }
    
    private val fragmentContainerView: FragmentContainerView
        get() = binding.fragmentContainerView
    
    protected val childNavController: NavController
        get() = fragmentContainerView.getFragment<NavHostFragment>().navController
    
    protected lateinit var navController: NavController
        private set
    
    fun setNavController(navController: NavController) {
        this.navController = navController
    }
    
    abstract fun setupNavigation(fragment: Fragment)
    
    fun onBackward(fragment: Fragment) {
        fragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        navController.navigateUp()
    }
    
    fun onLikesClicked(token: String, fragment: Fragment, comicViewModel: ComicViewModel, id: String) {
        comicViewModel.scopedUpdateLiked(
            token, id, fragment.lifecycleScope
        )
    }
    
    fun onCommentClicked(comicId: String, comicTitle: String, creator: String?) {
        navController.navigate(
            ComicDirections.toCommenting(comicId, comicTitle, creator)
        )
    }
    
    fun onBackPressed(): Boolean {
        val childNavController = childNavController
        if (childNavController.currentDestination?.id != R.id.metadata) {
            childNavController.navigate(R.id.to_metadata)
            return false
        }
        return true
    }
    
    open fun setupHeader(comicViewModel: ComicViewModel, mainViewModel: MainViewModel, lifecycleOwner: LifecycleOwner) = Unit
    
    abstract fun disableComment()
    
    abstract fun updateCommentCount(commentCount: Int)
    
    private class ComicLayoutCompatImpl(binding: FragmentComicBinding): ComicLayoutCompat(binding) {
    
        private val bottomNavigationView: BottomNavigationView
            get() = binding.bottomNavigationView!!
        
        override fun setupNavigation(fragment: Fragment) {
            bottomNavigationView.setupWithNavController(childNavController)
        }
    
        override fun disableComment() {
            bottomNavigationView.menu
                .findItem(R.id.comments)
                .isEnabled = false
        }
        
        override fun updateCommentCount(commentCount: Int) {
            bottomNavigationView.getOrCreateBadge(R.id.comments)
                .number = commentCount
        }
        
    }
    
    private class ComicLayoutCompatW600dpImpl(binding: FragmentComicBinding): ComicLayoutCompat(binding) {
    
        private val navigationRailView: NavigationRailView
            get() = binding.navigationRailView!!
        
        private val navHeaderComic: NavHeaderComicBinding
            = createAndSetNavHeaderComicBinding(navigationRailView)
        
        private fun createAndSetNavHeaderComicBinding(navigationRailView: NavigationRailView): NavHeaderComicBinding {
            val binding =  NavHeaderComicBinding.inflate(LayoutInflater.from(navigationRailView.context), navigationRailView, false)
            navigationRailView.addHeaderView(binding.root)
            return binding
        }
        
        override fun setupNavigation(fragment: Fragment) {
            navigationRailView.setupWithNavController(childNavController)
            navHeaderComic.fragment = fragment
        }
    
        override fun setupHeader(comicViewModel: ComicViewModel, mainViewModel: MainViewModel, lifecycleOwner: LifecycleOwner) {
            navHeaderComic.layoutCompat = this
            navHeaderComic.comicViewModel = comicViewModel
            navHeaderComic.mainViewModel = mainViewModel
            navHeaderComic.lifecycleOwner = lifecycleOwner
        }
    
        override fun disableComment() {
            navigationRailView.menu.findItem(R.id.comments)
                .isEnabled = false
        }
    
        override fun updateCommentCount(commentCount: Int) {
            navigationRailView.getOrCreateBadge(R.id.comments)
                .number = commentCount
        }
        
    }
    
    private class ComicLayoutCompatW1240dpImpl(binding: FragmentComicBinding): ComicLayoutCompat(binding) {
        
        private companion object {
            const val BADGE_MAX = 999
        }
        
        private val navigationView: NavigationView
            get() = binding.navigationView!!
    
        private val navHeaderComic: NavHeaderComicBinding
            = NavHeaderComicBinding.bind(navigationView.getHeaderView(0))
        
        private val like: ExtendedFloatingActionButton
            get() = navHeaderComic.extendedFloatingActionButtonLikes!!
        private val comment: ExtendedFloatingActionButton
            get() = navHeaderComic.extendedFloatingActionButtonComment!!
        
        override fun setupNavigation(fragment: Fragment) {
            navigationView.setupWithNavController(childNavController)
            navHeaderComic.lifecycleOwner = fragment.viewLifecycleOwner
            navHeaderComic.fragment = fragment
        }
    
        override fun setupHeader(comicViewModel: ComicViewModel, mainViewModel: MainViewModel, lifecycleOwner: LifecycleOwner) {
            navHeaderComic.layoutCompat = this
            navHeaderComic.comicViewModel = comicViewModel
            navHeaderComic.mainViewModel = mainViewModel
            navHeaderComic.lifecycleOwner = lifecycleOwner
            
            comment.hide()
            
            val onChangedCallback = object: OnChangedCallback() {
                override fun onHidden(extendedFab: ExtendedFloatingActionButton?) {
                    when (childNavController.currentDestination?.id) {
                        R.id.metadata -> {
                            like.show()
                        }
                        R.id.comments -> {
                            comment.show()
                        }
                    }
                }
            }
            
            childNavController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.metadata -> {
                        comment.hide(onChangedCallback)
                    }
                    R.id.comments -> {
                        like.hide(onChangedCallback)
                    }
                }
            }
        }
    
        override fun disableComment() {
            navigationView.menu.findItem(R.id.comments)
                .isEnabled = false
        }
    
        override fun updateCommentCount(commentCount: Int) {
            val binding = ComicNavigationMenuItemActionBinding.inflate(
                LayoutInflater.from(navigationView.context), navigationView, false
            )
            binding.commentCount = min(commentCount, BADGE_MAX)
            navigationView.menu.findItem(R.id.comments)
                .actionView = binding.root
        }
        
    }
    
}