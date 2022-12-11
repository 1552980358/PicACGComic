package projekt.cloud.piece.pic.ui.comicDetail

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.MenuProvider
import androidx.core.view.doOnPreDraw
import androidx.core.view.get
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.platform.Hold
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlin.math.abs
import projekt.cloud.piece.pic.Comic
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiComics.EpisodeResponseBody.Data.Episode
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentComicDetailBinding
import projekt.cloud.piece.pic.util.DisplayUtil.deviceBounds
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.NestedScrollViewUtil.isScrollable

class ComicDetailFragment: BaseFragment<FragmentComicDetailBinding>(), OnClickListener {

    companion object {
        private const val ARG_ID = "id"
    }
    
    private val appBarLayout: AppBarLayout
        get() = binding.appBarLayout
    private val collapsingToolbarLayout: CollapsingToolbarLayout
        get() = binding.collapsingToolbarLayout
    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    private val bottomAppBar: BottomAppBar
        get() = binding.bottomAppBar
    private val floatingActionButton: FloatingActionButton
        get() = binding.floatingActionButton
    private val tagGroup: ChipGroup
        get() = binding.chipGroup
    private val creator: MaterialCardView
        get() = binding.materialCardView
    private val creatorDetailIndicator: MaterialCheckBox
        get() = binding.materialCheckBox
    private val creatorDetail
        get() = binding.linearLayoutCompat
    private val nestedScrollView: NestedScrollView
        get() = binding.nestedScrollView
    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    private val comic: Comic by activityViewModels()
    
    private val docList: ArrayList<Episode.Doc>
        get() = comic.docList
    
    private lateinit var navController: NavController
    
    private var clearComicData = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        sharedElementEnterTransition = MaterialContainerTransform()
        exitTransition = Hold()
        if (args.containsKey(ARG_ID)) {
            comic.id = args.getString(ARG_ID)
        }
    }
    
    override fun setViewModels(binding: FragmentComicDetailBinding) {
        binding.applicationConfigs = applicationConfigs
        binding.comic = comic
    }
    
    override fun setUpToolbar() {
        setSupportActionBar(bottomAppBar)
        toolbar.setupWithNavController(navController)
    }
    
    override fun setUpViews() {
        postponeEnterTransition()
        
        collapsingToolbarLayout.updateLayoutParams<AppBarLayout.LayoutParams> {
            height = requireContext().deviceBounds.width() * 4 / 3
        }
    
        val fabMarginBottom = floatingActionButton.marginBottom
        applicationConfigs.windowInsetBottom.observe(viewLifecycleOwner) {
            floatingActionButton.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                updateMargins(bottom = fabMarginBottom + it)
            }
        }
        floatingActionButton.setOnClickListener(this)
    
        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, _, scrollY, _, _ ->
            when {
                scrollY >= v[0].measuredHeight - v.measuredHeight -> {
                    if (!bottomAppBar.isScrolledDown) {
                        bottomAppBar.performHide()
                    }
                }
                else -> {
                    if (!bottomAppBar.isScrolledUp) {
                        bottomAppBar.performShow()
                    }
                }
            }
        }
        comic.comic.observe(viewLifecycleOwner) {
            it?.tags?.let { tags -> addTags(tags) }
        }
        creator.setOnClickListener(this)
        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_comic_detail, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_likes -> {}
                    R.id.menu_star -> {}
                }
                return true
            }
        }, viewLifecycleOwner, State.CREATED)
    
        recyclerView.adapter = RecyclerViewAdapter(docList) { index, v ->
            launchToComicDetail(index, v)
        }
        recyclerView.doOnPreDraw { startPostponedEnterTransition() }
    
        applicationConfigs.token.observe(viewLifecycleOwner) {
            if (docList.isEmpty()) {
                comic.requestComicInfo(
                    it,
                    success = { (recyclerView.adapter as RecyclerViewAdapter).notifyDataUpdated() },
                    failed = { resId -> /*root.showSnack(resId)*/ }
                )
            }
        }
    
        appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            when {
                abs(verticalOffset) >= appBarLayout.totalScrollRange -> {
                    if (!nestedScrollView.isScrollable && !bottomAppBar.isScrolledDown) {
                        bottomAppBar.performHide()
                    }
                }
                else -> {
                    if (!nestedScrollView.isScrollable && !bottomAppBar.isScrolledUp) {
                        bottomAppBar.performShow()
                    }
                }
            }
        }
    }
    
    override val containerTransitionName: String?
        get() = args.getString(getString(R.string.comic_detail_transition))

    private fun addTags(tags: List<String>) {
        tags.forEach { tag ->
            tagGroup.addView(
                Chip(requireContext()).apply {
                    text = tag
                    isCloseIconVisible = false
                    setOnClickListener {}
                }
            )
        }
    }
    
    override fun onDestroyView() {
        if (clearComicData) {
            comic.clearAll()
        }
        super.onDestroyView()
    }
    
    override fun onClick(v: View?) {
        when (v) {
            creator -> {
                creatorDetail.visibility = when (creatorDetail.visibility) {
                    VISIBLE -> GONE
                    else -> VISIBLE
                }
                creatorDetailIndicator.isChecked = creatorDetail.visibility == VISIBLE
            }
            floatingActionButton -> launchToComicDetail(view = floatingActionButton)
        }
    }
    
    private fun launchToComicDetail(index: Int = 0, view: View) {
        clearComicData = false
        navController.navigate(
            ComicDetailFragmentDirections.actionComicDetailToReadFragment(view.transitionName, index),
            FragmentNavigatorExtras(view to view.transitionName)
        )
    }
    
}