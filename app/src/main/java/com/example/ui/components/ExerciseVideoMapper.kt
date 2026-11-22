package com.example.ui.components

import androidx.annotation.RawRes
import com.example.R
import com.example.data.local.entity.ExerciseEntity

object ExerciseVideoMapper {

    val codeToRawRes: Map<String, Int> = mapOf(
        // CALENTAMIENTO & RECUPERACIÓN
        "warmup_joint_mobility" to R.raw.vid_warmup_joint_mobility,
        "warmup_general_mobility" to R.raw.vid_warmup_general_mobility,
        "recovery_breathing_stretch" to R.raw.vid_recovery_breathing_stretch,

        // PECHO
        "chest_wall_pushups" to R.raw.vid_chest_wall_pushups,
        "chest_incline_pushups" to R.raw.vid_chest_incline_pushups,
        "chest_standard_pushups" to R.raw.vid_chest_standard_pushups,
        "chest_diamond_pushups" to R.raw.vid_chest_diamond_pushups,

        // BRAZOS
        "arms_mobility" to R.raw.vid_arms_mobility,
        "arms_chair_dips" to R.raw.vid_arms_chair_dips,
        "arms_pike_press" to R.raw.vid_arms_pike_press,
        "arms_shadow_boxing" to R.raw.vid_arms_shadow_boxing,

        // ABDOMEN
        "abs_dead_bug" to R.raw.vid_abs_dead_bug,
        "abs_plank_hold" to R.raw.vid_abs_plank_hold,
        "abs_bicycle_crunch" to R.raw.vid_abs_bicycle_crunch,

        // PIERNAS
        "legs_bodyweight_squat" to R.raw.vid_legs_bodyweight_squat,
        "legs_calf_raises" to R.raw.vid_legs_calf_raises,
        "legs_leg_mobility" to R.raw.vid_legs_leg_mobility,
        "legs_reverse_lunge" to R.raw.vid_legs_reverse_lunge,
        "legs_glute_bridge" to R.raw.vid_legs_glute_bridge,

        // ESPALDA
        "back_mobility" to R.raw.vid_back_mobility,
        "back_bird_dog" to R.raw.vid_back_bird_dog,
        "back_superman_hold" to R.raw.vid_back_superman_hold,
        "back_prone_yw_raises" to R.raw.vid_back_prone_yw_raises,

        // RESISTENCIA
        "endurance_march_high_knees" to R.raw.vid_endurance_march_high_knees,
        "endurance_brisk_walk" to R.raw.vid_endurance_brisk_walk,
        "endurance_low_impact_aerobics" to R.raw.vid_endurance_low_impact_aerobics,
        "endurance_jumping_jacks" to R.raw.vid_endurance_jumping_jacks,
        "endurance_mountain_climbers" to R.raw.vid_endurance_mountain_climbers,

        // MOVILIDAD
        "mobility_cat_cow" to R.raw.vid_mobility_cat_cow,
        "mobility_world_greatest_stretch" to R.raw.vid_mobility_world_greatest_stretch,

        // CUERPO COMPLETO
        "fullbody_squat_to_reach" to R.raw.vid_fullbody_squat_to_reach,
        "fullbody_step_back_burpee" to R.raw.vid_fullbody_step_back_burpee,
        "fullbody_inchworm_walkout" to R.raw.vid_fullbody_inchworm_walkout
    )

    @RawRes
    fun getRawVideoRes(exercise: ExerciseEntity): Int? {
        return codeToRawRes[exercise.code]
    }
}
