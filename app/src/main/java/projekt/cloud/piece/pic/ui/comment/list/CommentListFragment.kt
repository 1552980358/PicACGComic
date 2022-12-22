package projekt.cloud.piece.pic.ui.comment.list

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import projekt.cloud.piece.pic.base.BaseAuthFragment
import projekt.cloud.piece.pic.databinding.FragmentCommentListBinding
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar

class CommentListFragment: BaseAuthFragment<FragmentCommentListBinding>() {
    
    private lateinit var navController: NavController
    
    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }
    
    override fun setUpActionBar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { performBackPress() }
    }
    
    override fun setUpViews() {
    }
    
}