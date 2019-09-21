package com.example.seoulapp.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.example.seoulapp.R;

public class CommentMoreAct extends Activity {

    TextView Commentmore;

    public void MoreComment(View v){

        Commentmore = v.findViewById(R.id.watching_comment);
        Commentmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommentMoreAct.this, CommentFrag.class);
                startActivity(intent);
            }
        });

    }
}
