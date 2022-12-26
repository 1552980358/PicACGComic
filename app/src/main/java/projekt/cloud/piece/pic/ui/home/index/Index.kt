package projekt.cloud.piece.pic.ui.home.index

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentIndexBinding
import projekt.cloud.piece.pic.util.LayoutSizeMode.LayoutSizeModeUtil.getLayoutSize

class Index: BaseFragment<FragmentIndexBinding>() {

    private lateinit var indexLayoutHelper: IndexLayoutHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        indexLayoutHelper = IndexLayoutHelper(binding, requireActivity().getLayoutSize())
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSetupActionBar(binding: FragmentIndexBinding) {
        val navController = findNavController()
        indexLayoutHelper.setupActionBar(this, navController)
    }

}