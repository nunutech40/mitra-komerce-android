package id.android.kmabsensi.presentation.main

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_row_team.view.*
import id.android.kmabsensi.R
import id.android.kmabsensi.domain.Team
import id.android.kmabsensi.utils.loadImageFromUrl

class TeamItem(val team: Team) : Item(){

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.apply {
            itemView.imgTeam.loadImageFromUrl(team.teamLogo)
            itemView.txtTeam.text = team.teamName
        }

    }

    override fun getLayout(): Int = R.layout.item_row_team

}