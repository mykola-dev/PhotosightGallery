package ds.photosight.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment
import ds.photosight.Constants
import ds.photosight.utils.L

public abstract class ListFragmentAbstract : ListFragment(), Constants {

    protected var mPositionChecked: Int = 0
    protected var mPositionShown: Int = -1


    private fun isActive(): Boolean {
        return userVisibleHint
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        L.v("list on view created " + listType)

        mPositionChecked = getRoot().getListSelection(listType)
        listView.overScrollMode = View.OVER_SCROLL_NEVER

        val adapter = initList()
        listAdapter = adapter

        listView.choiceMode = AbsListView.CHOICE_MODE_SINGLE
        listView.setSelection(mPositionChecked)
        listView.setItemChecked(mPositionChecked, true)
        listView.setSelector(android.R.color.transparent)
    }


    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        showDetails(position)
    }

    private fun getRoot(): MainActivity {
        return activity as MainActivity
    }


    private fun showDetails(index: Int) {
        if (!isActive())
            return

        mPositionChecked = index
        listView.setItemChecked(index, true)

        getRoot().currPage = 0
        getRoot().selectItem(index)

        mPositionShown = index
    }


    protected abstract val listType: Int


    protected abstract fun initList(): ListAdapter?


    //
    // **********************************************************************************************************************************************
    // ListAdapter
    // **********************************************************************************************************************************************
    //
    class ListAdapter(context: Context, textViewResourceId: Int, objects: Array<String>) : ArrayAdapter<String>(context, textViewResourceId, objects) {

    }
}
