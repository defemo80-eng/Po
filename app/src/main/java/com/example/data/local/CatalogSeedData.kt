package com.example.data.local

import com.example.data.local.entity.AchievementEntity
import com.example.data.local.entity.ExerciseEntity
import com.example.data.local.entity.RoutineEntity

object CatalogSeedData {

    val defaultExercises: List<ExerciseEntity> = listOf(
        // Calentamiento y Recuperación
        ExerciseEntity(
            code = "warmup_joint_mobility",
            name = "Calentamiento Articular",
            category = "MOVILIDAD",
            explanation = "Prepara hombros, cadera, rodillas y tobillos elevando suavemente la temperatura muscular antes de entrenar.",
            instructions = "1. Realiza círculos amplios con los hombros hacia atrás durante 30 segundos.\n2. Gira suavemente la cadera en ambos sentidos.\n3. Eleva rodillas de forma alterna a ritmo cómodo.\n4. Rota tobillos y muñecas sin forzar.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 180,
            recommendedReps = "3 min continuos",
            restSeconds = 20,
            illustrationKey = "warmup",
            isWarmupOrRecovery = true
        ),
        ExerciseEntity(
            code = "recovery_breathing_stretch",
            name = "Estiramiento y Recuperación",
            category = "MOVILIDAD",
            explanation = "Relaja la musculatura después del esfuerzo, mejora la flexibilidad general y normaliza el ritmo respiratorio.",
            instructions = "1. Inhala profundo 4 segundos elevando brazos.\n2. Exhala lento bajando hacia las puntas sin rebotes.\n3. Estira cuádriceps sujetando un pie detrás con espalda recta.\n4. Mantén cada postura 25 segundos por lado.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 240,
            recommendedReps = "4 min suaves",
            restSeconds = 15,
            illustrationKey = "recovery",
            isWarmupOrRecovery = true
        ),

        // PECHO
        ExerciseEntity(
            code = "chest_incline_pushups",
            name = "Flexiones Inclinadas",
            category = "PECHO",
            explanation = "Versión amigable para principiantes que fortalece pectorales, hombros y tríceps reduciendo la carga articular.",
            instructions = "1. Apoya las manos en una superficie firme más alta que tus pies.\n2. Mantén el cuerpo alineado activando el abdomen.\n3. Baja el pecho con control hacia el borde.\n4. Empuja exhalando hasta extender los brazos.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 90,
            recommendedReps = "3 series x 10 reps",
            restSeconds = 45,
            illustrationKey = "pushup"
        ),
        ExerciseEntity(
            code = "chest_standard_pushups",
            name = "Flexiones Clásicas",
            category = "PECHO",
            explanation = "Ejercicio fundamental de peso corporal para desarrollar fuerza en pecho, deltoides anteriores y estabilidad del core.",
            instructions = "1. Coloca las manos ligeramente más abiertas que los hombros.\n2. Mantén la cadera estable sin hundir la zona lumbar.\n3. Flexiona codos a unos 45 grados respecto al torso.\n4. Sube con firmeza manteniendo la respiración fluida.",
            difficulty = "INTERMEDIO",
            durationSeconds = 120,
            recommendedReps = "3 series x 12 reps",
            restSeconds = 45,
            illustrationKey = "pushup"
        ),
        ExerciseEntity(
            code = "chest_diamond_pushups",
            name = "Flexiones Cerradas de Potencia",
            category = "PECHO",
            explanation = "Enfoca el trabajo en la zona interna del pectoral y potencia los tríceps con mayor exigencia de control.",
            instructions = "1. Junta las manos debajo del pecho formando un triángulo suave.\n2. Contrae glúteos y abdomen para evitar balanceos.\n3. Desciende lentamente hasta acercar el pecho a las manos.\n4. Empuja de forma controlada sin bloquear bruscamente.",
            difficulty = "AVANZADO",
            durationSeconds = 120,
            recommendedReps = "3 series x 10 reps",
            restSeconds = 60,
            illustrationKey = "pushup"
        ),

        // BRAZOS
        ExerciseEntity(
            code = "arms_chair_dips",
            name = "Fondos para Tríceps",
            category = "BRAZOS",
            explanation = "Fortalece la parte posterior del brazo y mejora la estabilidad de los hombros usando una silla estable.",
            instructions = "1. Siéntate en el borde de una silla firme y apoya las manos junto a la cadera.\n2. Adelanta un poco los pies manteniendo las rodillas flexionadas.\n3. Baja suavemente doblando los codos hacia atrás.\n4. Empuja hacia arriba manteniendo los hombros lejos de las orejas.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 90,
            recommendedReps = "3 series x 10 reps",
            restSeconds = 45,
            illustrationKey = "arms"
        ),
        ExerciseEntity(
            code = "arms_pike_press",
            name = "Empuje en V para Hombros",
            category = "BRAZOS",
            explanation = "Trabaja hombros y brazos con tu propio peso mejorando la fuerza de empuje vertical.",
            instructions = "1. Forma una V invertida con manos y pies en el suelo.\n2. Mira hacia tus pies manteniendo el cuello relajado.\n3. Flexiona los codos acercando la coronilla hacia el piso.\n4. Regresa extendiendo brazos de manera controlada.",
            difficulty = "INTERMEDIO",
            durationSeconds = 110,
            recommendedReps = "3 series x 8 reps",
            restSeconds = 50,
            illustrationKey = "arms"
        ),
        ExerciseEntity(
            code = "arms_shadow_boxing",
            name = "Boxeo de Sombra Dinámico",
            category = "BRAZOS",
            explanation = "Activa bíceps, tríceps, hombros y coordinación cardiovascular sin impacto en las articulaciones.",
            instructions = "1. Adopta una postura estable con una pierna ligeramente adelantada.\n2. Lanza golpes rectos y cruzados alternando brazos con control.\n3. Mantén la guardia alta protegiendo el rostro.\n4. Sostén un ritmo constante sin hiperextender los codos.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 120,
            recommendedReps = "3 rondas x 40 seg",
            restSeconds = 30,
            illustrationKey = "cardio"
        ),

        // ABDOMEN
        ExerciseEntity(
            code = "abs_dead_bug",
            name = "Bicho Muerto Controlado",
            category = "ABDOMEN",
            explanation = "Fortalece el abdomen profundo protegiendo la espalda baja mediante coordinación de brazos y piernas.",
            instructions = "1. Acuéstate boca arriba con rodillas flexionadas a 90° y brazos al techo.\n2. Pega suavemente la zona lumbar al suelo.\n3. Extiende brazo derecho y pierna izquierda sin arquear la espalda.\n4. Regresa al centro y alterna de lado exhalando.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 120,
            recommendedReps = "3 series x 12 alternadas",
            restSeconds = 35,
            illustrationKey = "core"
        ),
        ExerciseEntity(
            code = "abs_plank_hold",
            name = "Plancha Isométrica",
            category = "ABDOMEN",
            explanation = "Desarrolla resistencia del core, espalda baja y hombros mejorando tu postura diaria.",
            instructions = "1. Apoya antebrazos debajo de los hombros y puntas de los pies.\n2. Forma una línea recta desde la cabeza hasta los talones.\n3. Activa abdomen y glúteos respirando de forma continua.\n4. Evita dejar caer la cadera o elevarla demasiado.",
            difficulty = "INTERMEDIO",
            durationSeconds = 105,
            recommendedReps = "3 series x 35 seg",
            restSeconds = 40,
            illustrationKey = "plank"
        ),
        ExerciseEntity(
            code = "abs_bicycle_crunch",
            name = "Abdominales en Bicicleta",
            category = "ABDOMEN",
            explanation = "Trabaja recto abdominal y oblicuos con rotación controlada de torso.",
            instructions = "1. Túmbate boca arriba con manos suaves detrás de las orejas.\n2. Lleva el codo derecho hacia la rodilla izquierda mientras extiendes la otra pierna.\n3. Realiza el giro desde el torso sin tirar del cuello.\n4. Alterna con ritmo lento y consciente.",
            difficulty = "INTERMEDIO",
            durationSeconds = 120,
            recommendedReps = "3 series x 16 giros",
            restSeconds = 40,
            illustrationKey = "core"
        ),

        // PIERNAS
        ExerciseEntity(
            code = "legs_bodyweight_squat",
            name = "Sentadillas Funcionales",
            category = "PIERNAS",
            explanation = "Fortalece cuádriceps, glúteos y estabilidad de rodillas para mejorar movilidad y fuerza diaria.",
            instructions = "1. Coloca los pies al ancho de hombros con puntas ligeramente hacia afuera.\n2. Lleva la cadera atrás y abajo como si te sentaras.\n3. Mantén el pecho erguido y las rodillas alineadas con los pies.\n4. Sube empujando desde toda la planta del pie.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 120,
            recommendedReps = "3 series x 15 reps",
            restSeconds = 45,
            illustrationKey = "squat"
        ),
        ExerciseEntity(
            code = "legs_reverse_lunge",
            name = "Zancadas Hacia Atrás",
            category = "PIERNAS",
            explanation = "Entrena cada pierna de forma independiente mejorando equilibrio, glúteos y control articular.",
            instructions = "1. De pie y erguido, da un paso largo hacia atrás.\n2. Flexiona ambas rodillas suavemente hasta formar ángulos cercanos a 90°.\n3. Mantén el peso distribuido sobre el talón delantero.\n4. Vuelve al centro y alterna la pierna.",
            difficulty = "INTERMEDIO",
            durationSeconds = 120,
            recommendedReps = "3 series x 12 alternadas",
            restSeconds = 45,
            illustrationKey = "squat"
        ),
        ExerciseEntity(
            code = "legs_glute_bridge",
            name = "Puente de Glúteos",
            category = "PIERNAS",
            explanation = "Activa glúteos e isquiotibiales mientras descarga la tensión acumulada en la zona lumbar.",
            instructions = "1. Acuéstate boca arriba con rodillas dobladas y pies firmes en el piso.\n2. Eleva la cadera apretando glúteos hasta alinear rodillas y hombros.\n3. Sostén 2 segundos arriba sin hiperextender la espalda.\n4. Baja despacio vértebra por vértebra.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 100,
            recommendedReps = "3 series x 15 reps",
            restSeconds = 35,
            illustrationKey = "core"
        ),

        // ESPALDA
        ExerciseEntity(
            code = "back_superman_hold",
            name = "Extensión Dorsal Superman",
            category = "ESPALDA",
            explanation = "Refuerza erectores espinales, espalda alta y postura para contrarrestar horas sentado.",
            instructions = "1. Túmbate boca abajo con brazos extendidos al frente.\n2. Eleva suavemente pecho, brazos y piernas unos centímetros del suelo.\n3. Mantén la mirada hacia abajo para alinear el cuello.\n4. Sostén 3 segundos y baja con control.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 100,
            recommendedReps = "3 series x 10 reps",
            restSeconds = 40,
            illustrationKey = "back"
        ),
        ExerciseEntity(
            code = "back_bird_dog",
            name = "Perro-Pájaro de Estabilidad",
            category = "ESPALDA",
            explanation = "Mejora el equilibrio postural y coordina la musculatura profunda de espalda y abdomen.",
            instructions = "1. Apóyate en cuatro puntos con manos bajo hombros y rodillas bajo cadera.\n2. Extiende el brazo derecho adelante y la pierna izquierda atrás.\n3. Mantén la pelvis nivelada sin rotar el tronco.\n4. Pausa 2 segundos y cambia de lado.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 110,
            recommendedReps = "3 series x 12 alternadas",
            restSeconds = 35,
            illustrationKey = "back"
        ),
        ExerciseEntity(
            code = "back_prone_yw_raises",
            name = "Elevaciones Y-W para Postura",
            category = "ESPALDA",
            explanation = "Activa romboides, trapecio medio y rotadores de hombro para una espalda fuerte y alineada.",
            instructions = "1. Boca abajo con frente apoyada en una toalla o cerca del suelo.\n2. Eleva los brazos formando una Y con pulgares hacia arriba.\n3. Lleva los codos hacia las costillas formando una W.\n4. Junta suavemente las escápulas en cada repetición.",
            difficulty = "INTERMEDIO",
            durationSeconds = 110,
            recommendedReps = "3 series x 12 reps",
            restSeconds = 40,
            illustrationKey = "back"
        ),

        // RESISTENCIA
        ExerciseEntity(
            code = "endurance_march_high_knees",
            name = "Marcha Activa / Rodillas Altas",
            category = "RESISTENCIA",
            explanation = "Eleva el pulso cardíaco progresivamente mejorando la capacidad aeróbica en casa o al aire libre.",
            instructions = "1. Inicia con una marcha en el sitio moviendo los brazos con energía.\n2. Eleva las rodillas a la altura de la cadera alternando piernas.\n3. Aterriza suave sobre la parte delantera del pie.\n4. Ajusta la velocidad según tu nivel de aire.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 120,
            recommendedReps = "3 intervalos x 40 seg",
            restSeconds = 30,
            illustrationKey = "cardio"
        ),
        ExerciseEntity(
            code = "endurance_jumping_jacks",
            name = "Saltos de Tijera Controlados",
            category = "RESISTENCIA",
            explanation = "Clásico ejercicio cardiovascular de cuerpo completo para potenciar resistencia y agilidad.",
            instructions = "1. Parte de pie con brazos a los lados.\n2. Abre piernas lateralmente mientras subes brazos por encima de la cabeza.\n3. Regresa al centro con un salto ligero o paso lateral si prefieres bajo impacto.\n4. Mantén el abdomen firme todo el tiempo.",
            difficulty = "INTERMEDIO",
            durationSeconds = 120,
            recommendedReps = "3 intervalos x 40 seg",
            restSeconds = 30,
            illustrationKey = "cardio"
        ),
        ExerciseEntity(
            code = "endurance_mountain_climbers",
            name = "Escaladores Dinámicos",
            category = "RESISTENCIA",
            explanation = "Combina trabajo cardiovascular intenso con estabilidad abdominal y fuerza en hombros.",
            instructions = "1. Colócate en posición de plancha alta con manos firmes.\n2. Lleva una rodilla hacia el pecho de manera alterna.\n3. Mantén la cadera baja y estable durante el movimiento.\n4. Respira a ritmo constante sin contener el aire.",
            difficulty = "AVANZADO",
            durationSeconds = 105,
            recommendedReps = "3 intervalos x 35 seg",
            restSeconds = 40,
            illustrationKey = "plank"
        ),

        // MOVILIDAD
        ExerciseEntity(
            code = "mobility_cat_cow",
            name = "Gato-Camello Espinal",
            category = "MOVILIDAD",
            explanation = "Libera rigidez en toda la columna vertebral y coordina respiración con movimiento.",
            instructions = "1. Colócate en cuatro apoyos sobre una superficie cómoda.\n2. Al inhalar, abre el pecho suavemente mirando adelante.\n3. Al exhalar, redondea la espalda empujando el suelo.\n4. Muévete lento disfrutando el rango natural.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 90,
            recommendedReps = "2 series x 10 ciclos",
            restSeconds = 20,
            illustrationKey = "mobility"
        ),
        ExerciseEntity(
            code = "mobility_world_greatest_stretch",
            name = "Apertura de Cadera y Torso",
            category = "MOVILIDAD",
            explanation = "Moviliza caderas, isquiotibiales y columna torácica en una secuencia fluida.",
            instructions = "1. Da una zancada amplia hacia adelante apoyando la mano contraria en el suelo.\n2. Rota el brazo del lado de la pierna adelantada hacia el techo.\n3. Sigue tu mano con la mirada respirando hondo.\n4. Cambia de pierna tras 5 respiraciones.",
            difficulty = "INTERMEDIO",
            durationSeconds = 120,
            recommendedReps = "2 series x 6 por lado",
            restSeconds = 25,
            illustrationKey = "mobility"
        ),

        // CUERPO COMPLETO
        ExerciseEntity(
            code = "fullbody_squat_to_reach",
            name = "Sentadilla con Extensión",
            category = "CUERPO_COMPLETO",
            explanation = "Integra piernas, glúteos, hombros y core en un movimiento fluido apto para todo nivel.",
            instructions = "1. Realiza una sentadilla controlada bajando la cadera.\n2. Al subir, extiende ambos brazos hacia el techo poniéndote ligeramente en puntas.\n3. Controla el descenso antes de la siguiente repetición.\n4. Mantén el ritmo constante y seguro.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 120,
            recommendedReps = "3 series x 12 reps",
            restSeconds = 40,
            illustrationKey = "fullbody"
        ),
        ExerciseEntity(
            code = "fullbody_step_back_burpee",
            name = "Burpee Sin Impacto (Paso Atrás)",
            category = "CUERPO_COMPLETO",
            explanation = "Activa todo el cuerpo elevando la capacidad física sin golpear rodillas ni muñecas.",
            instructions = "1. Baja las manos hacia el suelo flexionando rodillas.\n2. Lleva un pie atrás y luego el otro hasta quedar en plancha.\n3. Regresa dando pasos adelante y ponte de pie con brazos arriba.\n4. Mantén una técnica limpia en cada repetición.",
            difficulty = "INTERMEDIO",
            durationSeconds = 120,
            recommendedReps = "3 series x 10 reps",
            restSeconds = 45,
            illustrationKey = "fullbody"
        ),
        ExerciseEntity(
            code = "fullbody_inchworm_walkout",
            name = "Caminata de Gusano Completa",
            category = "CUERPO_COMPLETO",
            explanation = "Combina flexibilidad posterior, fuerza de hombros y estabilidad abdominal en un movimiento completo.",
            instructions = "1. De pie, inclínate suavemente hasta apoyar las manos delante de tus pies.\n2. Camina con las manos hacia adelante hasta alcanzar una plancha estable.\n3. Pausa 2 segundos apretando el abdomen.\n4. Camina con las manos de regreso y incorpórate.",
            difficulty = "AVANZADO",
            durationSeconds = 120,
            recommendedReps = "3 series x 8 reps",
            restSeconds = 45,
            illustrationKey = "fullbody"
        )
    )

    val defaultRoutines: List<RoutineEntity> = listOf(
        RoutineEntity(
            code = "routine_beginner_start",
            name = "Despegue Principiante",
            description = "Rutina segura de bajo impacto para construir hábito, energía diaria y coordinación.",
            difficulty = "PRINCIPIANTE",
            estimatedMinutes = 15,
            focusGoal = "PRINCIPIANTE",
            exerciseCodesCsv = "warmup_joint_mobility,legs_bodyweight_squat,chest_incline_pushups,abs_dead_bug,recovery_breathing_stretch"
        ),
        RoutineEntity(
            code = "routine_endurance_flow",
            name = "Circuito Resistencia Activa",
            description = "Eleva tu capacidad aeróbica y resistencia muscular con transiciones claras y pausas medidas.",
            difficulty = "INTERMEDIO",
            estimatedMinutes = 20,
            focusGoal = "RESISTENCIA",
            exerciseCodesCsv = "warmup_joint_mobility,endurance_march_high_knees,endurance_jumping_jacks,fullbody_step_back_burpee,abs_plank_hold,recovery_breathing_stretch"
        ),
        RoutineEntity(
            code = "routine_strength_core",
            name = "Fuerza Funcional y Core",
            description = "Fortalece pecho, brazos, espalda y abdomen con progresiones técnicas de peso corporal.",
            difficulty = "INTERMEDIO",
            estimatedMinutes = 25,
            focusGoal = "FUERZA",
            exerciseCodesCsv = "warmup_joint_mobility,chest_standard_pushups,arms_chair_dips,back_superman_hold,abs_plank_hold,legs_reverse_lunge,recovery_breathing_stretch"
        ),
        RoutineEntity(
            code = "routine_mobility_posture",
            name = "Movilidad y Espalda Sana",
            description = "Libera tensión postural, mejora flexibilidad y cuida tu columna en pocos minutos.",
            difficulty = "PRINCIPIANTE",
            estimatedMinutes = 15,
            focusGoal = "MOVILIDAD",
            exerciseCodesCsv = "warmup_joint_mobility,mobility_cat_cow,back_bird_dog,mobility_world_greatest_stretch,legs_glute_bridge,recovery_breathing_stretch"
        )
    )

    val defaultAchievements: List<AchievementEntity> = listOf(
        AchievementEntity(
            code = "first_steps",
            name = "Primeros pasos",
            description = "Registra tus primeros 100 pasos reales con DeFitt.",
            badgeTier = "GREEN",
            coinsReward = 10,
            requirementType = "STEPS_DAY",
            requirementTarget = 100
        ),
        AchievementEntity(
            code = "first_walk",
            name = "Primera caminata",
            description = "Alcanza 1,500 pasos en un mismo día.",
            badgeTier = "GREEN",
            coinsReward = 20,
            requirementType = "STEPS_DAY",
            requirementTarget = 1500
        ),
        AchievementEntity(
            code = "daily_goal_reached",
            name = "Meta diaria cumplida",
            description = "Completa el 100% de tu objetivo diario de pasos.",
            badgeTier = "GOLD",
            coinsReward = 35,
            requirementType = "STEP_GOAL",
            requirementTarget = 1
        ),
        AchievementEntity(
            code = "first_meal_logged",
            name = "Nutrición consciente",
            description = "Registra tu primera comida saludable en DeFitt.",
            badgeTier = "GREEN",
            coinsReward = 15,
            requirementType = "MEALS_TOTAL",
            requirementTarget = 1
        ),
        AchievementEntity(
            code = "first_workout",
            name = "Primera rutina completada",
            description = "Completa tu primer ejercicio guiado en DeFitt.",
            badgeTier = "GREEN",
            coinsReward = 20,
            requirementType = "EXERCISES_TOTAL",
            requirementTarget = 1
        ),
        AchievementEntity(
            code = "ten_workouts",
            name = "Diez ejercicios completados",
            description = "Acumula 10 ejercicios completados en tu historial.",
            badgeTier = "GOLD",
            coinsReward = 50,
            requirementType = "EXERCISES_TOTAL",
            requirementTarget = 10
        ),
        AchievementEntity(
            code = "beat_record",
            name = "Nuevo récord de actividad",
            description = "Supera tu mejor marca diaria anterior de pasos.",
            badgeTier = "GOLD",
            coinsReward = 40,
            requirementType = "BEAT_RECORD",
            requirementTarget = 1
        ),
        AchievementEntity(
            code = "three_day_streak",
            name = "Constancia saludable",
            description = "Mantén actividad registrada durante 3 días distintos.",
            badgeTier = "GREEN",
            coinsReward = 30,
            requirementType = "ACTIVE_DAYS",
            requirementTarget = 3
        ),
        AchievementEntity(
            code = "first_active_week",
            name = "Primera semana activa",
            description = "Suma 7 días con actividad registrada en DeFitt.",
            badgeTier = "GOLD",
            coinsReward = 75,
            requirementType = "ACTIVE_DAYS",
            requirementTarget = 7
        )
    )
}
