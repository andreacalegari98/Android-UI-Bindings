package com.nearit.ui_bindings_kotlin_sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nearit.ui_bindings.NearITUIBindings
import com.nearit.ui_bindings_kotlin_sample.factories.ContentFactory

/**
 * Created by Federico Boschini on 12/10/17.
 */
class ContentPlainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plain_content)

        val content = ContentFactory.getCompleteContent()

        val contentFragment = NearITUIBindings.getInstance(this@ContentPlainActivity)
                .createContentDetailFragmentBuilder(content)
                .build()

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_container, contentFragment)
                .commit()

    }

}