package org.ligi.gobandroid_hd.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

class DefaultGameExtrasFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = TextView(this.activity).apply {
        text = "Implement me!"
    }

}
