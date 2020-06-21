package me.m123.video.ui

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import me.m123.video.R

class AboutActivity: BaseAAppCompatActivity() {

    private lateinit var CheckUpdate: LinearLayout
    private lateinit var HelpFeedback: LinearLayout
    private lateinit var License: TextView
    private lateinit var NowVersion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.initUI()
        this.NowVersion.text = this.packetInfo.versionName

        this.CheckUpdate.setOnClickListener {
            this.checkUpdate(this)
        }

        this.HelpFeedback.setOnClickListener {

        }

        this.License.setOnClickListener {
            val intent = Intent(this, LicenseActivity::class.java)
            startActivity(intent)
        }

    }

    fun initUI() {
        this.CheckUpdate = this.findViewById(R.id.check_update)
        this.License = this.findViewById(R.id.about_license)
        this.NowVersion = this.findViewById(R.id.now_version)
        this.HelpFeedback = this.findViewById(R.id.help_feedback)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_about
    }

    override fun getToolbarTitle(): Int {
        return R.string.nav_name_about
    }
}