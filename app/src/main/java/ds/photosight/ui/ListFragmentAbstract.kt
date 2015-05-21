package ds.photosight.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.flurry.android.FlurryAgent
import ds.photosight.Constants
import ds.photosight.R
import ds.photosight.utils.L
import java.util.HashMap

public abstract class ListFragmentAbstract : ListFragment(), Constants {

    protected var mPositionChecked: Int = 0
    protected var mPositionShown: Int = -1


    private fun isActive(): Boolean {
        return getUserVisibleHint()
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super<ListFragment>.onViewCreated(view, savedInstanceState)
        L.v("list on view created " + getListType())

        mPositionChecked = getRoot().getListSelection(getListType())
        if (Build.VERSION.SDK_INT > 8)
            getListView().setOverScrollMode(View.OVER_SCROLL_NEVER)

        val adapter = initList()
        setListAdapter(adapter)

        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE)
        getListView().setSelection(mPositionChecked)
        getListView().setItemChecked(mPositionChecked, true)
        getListView().setSelector(android.R.color.transparent)
    }


    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {

        showDetails(position)

        flurry((v!!.findViewById(android.R.id.text1) as TextView).getText().toString())

        super<ListFragment>.onListItemClick(l, v, position, id)
    }


    private fun flurry(text: String) {
        val args = HashMap<String, String>()
        args.put("Category", text)
        args.put("Tab", getListType().toString())
        FlurryAgent.logEvent(Constants.LIST_CLICKED, args)
        Log.d(args.get("Tab"), args.get("Category"))
    }


    override fun onSaveInstanceState(b: Bundle?) {
        /*b.putInt("item", mPositionShown);
		if (!App.getInstance().isPortrait())
			b.putBoolean("fromLandscape", true);*/
        super<ListFragment>.onSaveInstanceState(b)
    }


    private fun getRoot(): MainActivity {
        return getActivity() as MainActivity
    }


    fun showDetails(index: Int) {
        if (!isActive())
            return

        mPositionChecked = index
        getListView().setItemChecked(index, true)

        getRoot().currPage = 0
        getRoot().selectItem(index)

        mPositionShown = index
    }


    protected abstract fun getListType(): Int


    protected abstract fun initList(): ListAdapter?


    //
    // **********************************************************************************************************************************************
    // ListAdapter
    // **********************************************************************************************************************************************
    //
    public class ListAdapter(context: Context, textViewResourceId: Int, objects: Array<String>) : ArrayAdapter<String>(context, textViewResourceId, objects) {


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)

           /* if (position == mPositionChecked) {
                v.setBackgroundResource(R.drawable.selector_color_solid)
            } else {
                v.setBackgroundResource(R.drawable.list_selector)
            }*/

            return v
        }

    }
}
