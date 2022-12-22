package projekt.cloud.piece.pic.ui.comment

import android.os.Bundle
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentCommentBinding

class CommentFragment: BaseFragment<FragmentCommentBinding>() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }
    
    override fun setUpViews() = Unit
    
}