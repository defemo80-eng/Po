package com.example.data.local

import com.example.data.local.entity.AchievementEntity
import com.example.data.local.entity.ExerciseEntity
import com.example.data.local.entity.RoutineEntity

object CatalogSeedData {

    val defaultExercises: List<ExerciseEntity> = listOf(
        // CALENTAMIENTO
        ExerciseEntity(
            code = "warmup_joint_mobility",
            name = "Calentamiento Articular",
            category = "CALENTAMIENTO",
            explanation = "Prepara hombros, cadera, rodillas y tobillos lubricando las articulaciones antes de entrenar.",
            instructions = "1. Realiza círculos amplios con los hombros hacia atrás durante 30 segundos.\n2. Gira suavemente la cadera en ambos sentidos.\n3. Eleva rodillas de forma alterna a ritmo cómodo.\n4. Rota tobillos y muñecas sin forzar.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 180,
            recommendedReps = "3 min continuos",
            restSeconds = 20,
            illustrationKey = "warmup",
            isWarmupOrRecovery = true,
            safetyWarning = "Realiza giros suaves dentro de un rango cómodo; detente si sientes chasquidos dolorosos.",
            videoResName = "vid_warmup_joint_mobility"
        ),
        ExerciseEntity(
            code = "warmup_general_mobility",
            name = "Movilidad General de Activación",
            category = "CALENTAMIENTO",
            explanation = "Eleva progresivamente el pulso y coordina brazos, torso y piernas para preparar todo el cuerpo.",
            instructions = "1. Mantén una postura erguida con pies al ancho de cadera.\n2. Balancea brazos hacia arriba y al frente al ritmo de tu respiración.\n3. Añade pasos suaves laterales o elevaciones ligeras de rodilla.\n4. Mantén movimientos fluidos y continuos.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 180,
            recommendedReps = "3 min progresivos",
            restSeconds = 20,
            illustrationKey = "warmup",
            isWarmupOrRecovery = true,
            safetyWarning = "Comienza lento los primeros 30 segundos para permitir que el ritmo cardíaco suba gradualmente.",
            videoResName = "vid_warmup_general_mobility"
        ),

        // RECUPERACIÓN
        ExerciseEntity(
            code = "recovery_breathing_stretch",
            name = "Estiramientos Suaves y Recuperación",
            category = "RECUPERACION",
            explanation = "Relaja la musculatura después del esfuerzo, mejora la flexibilidad general y normaliza la respiración.",
            instructions = "1. Inhala profundo 4 segundos elevando brazos.\n2. Exhala lento bajando hacia las puntas sin rebotes.\n3. Estira cuádriceps sujetando un pie detrás con espalda recta.\n4. Mantén cada postura 25 segundos por lado.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 240,
            recommendedReps = "4 min suaves",
            restSeconds = 15,
            illustrationKey = "recovery",
            isWarmupOrRecovery = true,
            safetyWarning = "Nunca hagas rebotes al estirar; busca tensión agradable sin dolor agudo.",
            videoResName = "vid_recovery_breathing_stretch"
        ),

        // PECHO
        ExerciseEntity(
            code = "chest_wall_pushups",
            name = "Flexiones de Pared",
            category = "PECHO",
            explanation = "Opción segura y accesible para activar pecho, hombros y brazos sin sobrecargar muñecas ni espalda.",
            instructions = "1. Párate frente a una pared firme a un paso de distancia.\n2. Apoya las palmas a la altura y ancho de los hombros.\n3. Flexiona los codos acercando el pecho hacia la pared manteniendo el cuerpo recto.\n4. Empuja con control hasta extender brazos sin bloquear.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 90,
            recommendedReps = "3 series x 12 reps",
            restSeconds = 35,
            illustrationKey = "pushup",
            safetyWarning = "Evita que las caderas se hundan hacia la pared; mantén el abdomen ligeramente activo.",
            videoResName = "vid_chest_wall_pushups"
        ),
        ExerciseEntity(
            code = "chest_incline_pushups",
            name = "Flexiones Adaptadas (Inclinadas)",
            category = "PECHO",
            explanation = "Versión adaptada que fortalece pectorales, hombros y tríceps reduciendo la carga articular.",
            instructions = "1. Apoya las manos en una superficie firme más alta que tus pies (o rodillas en colchoneta).\n2. Mantén el cuerpo alineado activando el abdomen.\n3. Baja el pecho con control hacia el borde.\n4. Empuja exhalando hasta extender los brazos.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 90,
            recommendedReps = "3 series x 10 reps",
            restSeconds = 45,
            illustrationKey = "pushup",
            safetyWarning = "Asegúrate de que el apoyo elegido no resbale antes de cargar peso sobre las manos.",
            videoResName = "vid_chest_incline_pushups"
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
            illustrationKey = "pushup",
            safetyWarning = "Si notas molestia en la espalda baja, apoya las rodillas o pasa a flexiones inclinadas.",
            videoResName = "vid_chest_standard_pushups"
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
            illustrationKey = "pushup",
            safetyWarning = "Detente si percibes pinchazo en muñecas o codos; mantén los codos cerca del torso.",
            videoResName = "vid_chest_diamond_pushups"
        ),

        // BRAZOS
        ExerciseEntity(
            code = "arms_mobility",
            name = "Movilidad de Brazos y Hombros",
            category = "BRAZOS",
            explanation = "Desbloquea hombros, codos y muñecas mejorando la amplitud de movimiento y reduciendo rigidez postural.",
            instructions = "1. De pie con espalda recta, eleva ambos brazos lateralmente hasta arriba.\n2. Dibuja círculos amplios hacia adelante y hacia atrás.\n3. Flexiona y extiende codos rotando suavemente las muñecas.\n4. Respira profundo sincronizando el movimiento.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 90,
            recommendedReps = "2 series x 45 seg",
            restSeconds = 20,
            illustrationKey = "warmup",
            safetyWarning = "No subas los hombros hacia las orejas; realiza el movimiento sin dolor articular.",
            videoResName = "vid_arms_mobility"
        ),
        ExerciseEntity(
            code = "arms_chair_dips",
            name = "Fuerza Adaptada de Brazos (Fondos)",
            category = "BRAZOS",
            explanation = "Ejercicio adaptado de fuerza para tonificar tríceps y hombros usando una silla estable.",
            instructions = "1. Siéntate en el borde de una silla firme y apoya las manos junto a la cadera.\n2. Adelanta un poco los pies manteniendo las rodillas flexionadas.\n3. Baja suavemente doblando los codos hacia atrás.\n4. Empuja hacia arriba manteniendo los hombros lejos de las orejas.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 90,
            recommendedReps = "3 series x 10 reps",
            restSeconds = 45,
            illustrationKey = "arms",
            safetyWarning = "Usa una silla apoyada contra la pared y no bajes más allá de 90° en los codos.",
            videoResName = "vid_arms_chair_dips"
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
            illustrationKey = "arms",
            safetyWarning = "Flexiona levemente las rodillas si sientes demasiada tensión en la parte posterior de las piernas.",
            videoResName = "vid_arms_pike_press"
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
            illustrationKey = "cardio",
            safetyWarning = "Frena el golpe justo antes de extender el codo por completo para proteger la articulación.",
            videoResName = "vid_arms_shadow_boxing"
        ),

        // ABDOMEN
        ExerciseEntity(
            code = "abs_dead_bug",
            name = "Estabilidad Abdominal (Bicho Muerto)",
            category = "ABDOMEN",
            explanation = "Fortalece la faja abdominal profunda protegiendo la espalda baja mediante coordinación controlada.",
            instructions = "1. Acuéstate boca arriba con rodillas flexionadas a 90° y brazos al techo.\n2. Pega suavemente la zona lumbar al suelo.\n3. Extiende brazo derecho y pierna izquierda sin arquear la espalda.\n4. Regresa al centro y alterna de lado exhalando.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 120,
            recommendedReps = "3 series x 12 alternadas",
            restSeconds = 35,
            illustrationKey = "core",
            safetyWarning = "Si la espalda baja se despega del suelo, baja la pierna menos o flexiónala un poco más.",
            videoResName = "vid_abs_dead_bug"
        ),
        ExerciseEntity(
            code = "abs_plank_hold",
            name = "Plancha Adaptada e Isométrica",
            category = "ABDOMEN",
            explanation = "Desarrolla resistencia del core, espalda baja y hombros mejorando tu postura diaria (puede hacerse sobre rodillas).",
            instructions = "1. Apoya antebrazos debajo de los hombros y rodillas o puntas de los pies.\n2. Forma una línea recta desde la cabeza hasta talones o rodillas.\n3. Activa abdomen y glúteos respirando de forma continua.\n4. Evita dejar caer la cadera o elevarla demasiado.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 105,
            recommendedReps = "3 series x 30 seg",
            restSeconds = 40,
            illustrationKey = "plank",
            safetyWarning = "En versión adaptada apoya las rodillas; detén la serie si sientes carga en la zona lumbar.",
            videoResName = "vid_abs_plank_hold"
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
            illustrationKey = "core",
            safetyWarning = "No jales la cabeza con las manos; dirige el hombro hacia la rodilla opuesta.",
            videoResName = "vid_abs_bicycle_crunch"
        ),

        // PIERNAS
        ExerciseEntity(
            code = "legs_bodyweight_squat",
            name = "Sentadillas Adaptadas y Funcionales",
            category = "PIERNAS",
            explanation = "Fortalece cuádriceps, glúteos y rodillas. Puede realizarse con silla detrás como guía segura.",
            instructions = "1. Coloca los pies al ancho de hombros con puntas ligeramente hacia afuera.\n2. Lleva la cadera atrás y abajo como si te sentaras.\n3. Mantén el pecho erguido y las rodillas alineadas con los pies.\n4. Sube empujando desde toda la planta del pie.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 120,
            recommendedReps = "3 series x 15 reps",
            restSeconds = 45,
            illustrationKey = "squat",
            safetyWarning = "Evita que las rodillas se junten hacia adentro; reduce la profundidad si hay molestia.",
            videoResName = "vid_legs_bodyweight_squat"
        ),
        ExerciseEntity(
            code = "legs_calf_raises",
            name = "Elevación de Talones",
            category = "PIERNAS",
            explanation = "Fortalece gemelos, tobillos y estabilidad del pie para caminar o correr con mayor seguridad.",
            instructions = "1. Párate erguido con pies al ancho de cadera (puedes apoyar dedos en una pared).\n2. Eleva ambos talones subiendo sobre las puntas de los pies.\n3. Sostén 1 segundo arriba apretando pantorrillas.\n4. Desciende lentamente hasta apoyar los talones.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 90,
            recommendedReps = "3 series x 15 reps",
            restSeconds = 30,
            illustrationKey = "squat",
            safetyWarning = "Apoya una mano en pared o silla si necesitas mejor equilibrio.",
            videoResName = "vid_legs_calf_raises"
        ),
        ExerciseEntity(
            code = "legs_leg_mobility",
            name = "Movilidad de Piernas y Cadera",
            category = "PIERNAS",
            explanation = "Mejora el rango articular de caderas, rodillas e isquiotibiales previniendo sobrecargas.",
            instructions = "1. Apóyate suavemente en una pared o silla si lo deseas.\n2. Balancea una pierna adelante y atrás de forma controlada.\n3. Realiza círculos amplios de rodilla abriendo la cadera.\n4. Cambia de pierna tras 8 repeticiones fluidas.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 100,
            recommendedReps = "2 series x 10 por pierna",
            restSeconds = 25,
            illustrationKey = "mobility",
            safetyWarning = "No fuerces el balanceo; aumenta la amplitud poco a poco conforme entra en calor la cadera.",
            videoResName = "vid_legs_leg_mobility"
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
            illustrationKey = "squat",
            safetyWarning = "Mantén el torso vertical y apoya suavemente la rodilla si requieres menor profundidad.",
            videoResName = "vid_legs_reverse_lunge"
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
            illustrationKey = "core",
            safetyWarning = "Empuja desde los talones y evita arquear en exceso la parte baja de la espalda.",
            videoResName = "vid_legs_glute_bridge"
        ),

        // ESPALDA
        ExerciseEntity(
            code = "back_mobility",
            name = "Movilidad de Espalda y Hombros",
            category = "ESPALDA",
            explanation = "Alivia rigidez dorsal y lumbar mediante rotaciones y flexiones suaves de columna.",
            instructions = "1. De pie o sentado erguido, entrelaza manos al frente redondeando suavemente la espalda.\n2. Luego abre los brazos juntando escápulas y abriendo el pecho.\n3. Inclina ligeramente el torso a cada lado exhalando lento.\n4. Repite con ritmo relajado y continuo.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 90,
            recommendedReps = "2 series x 10 ciclos",
            restSeconds = 20,
            illustrationKey = "back",
            safetyWarning = "Muévete sin tirones bruscos, especialmente si pasas muchas horas sentado.",
            videoResName = "vid_back_mobility"
        ),
        ExerciseEntity(
            code = "back_bird_dog",
            name = "Postural Perro-Pájaro",
            category = "ESPALDA",
            explanation = "Mejora el equilibrio postural y coordina la musculatura profunda de espalda y abdomen.",
            instructions = "1. Apóyate en cuatro puntos con manos bajo hombros y rodillas bajo cadera.\n2. Extiende el brazo derecho adelante y la pierna izquierda atrás.\n3. Mantén la pelvis nivelada sin rotar el tronco.\n4. Pausa 2 segundos y cambia de lado.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 110,
            recommendedReps = "3 series x 12 alternadas",
            restSeconds = 35,
            illustrationKey = "back",
            safetyWarning = "Coloca una toalla bajo las rodillas si sientes presión en el suelo.",
            videoResName = "vid_back_bird_dog"
        ),
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
            illustrationKey = "back",
            safetyWarning = "Eleva solo unos centímetros sin forzar la zona lumbar ni mirar hacia arriba.",
            videoResName = "vid_back_superman_hold"
        ),
        ExerciseEntity(
            code = "back_prone_yw_raises",
            name = "Ejercicios Posturales Y-W",
            category = "ESPALDA",
            explanation = "Activa romboides, trapecio medio y rotadores de hombro para una espalda fuerte y erguida.",
            instructions = "1. Boca abajo con frente apoyada en una toalla o cerca del suelo.\n2. Eleva los brazos formando una Y con pulgares hacia arriba.\n3. Lleva los codos hacia las costillas formando una W.\n4. Junta suavemente las escápulas en cada repetición.",
            difficulty = "INTERMEDIO",
            durationSeconds = 110,
            recommendedReps = "3 series x 12 reps",
            restSeconds = 40,
            illustrationKey = "back",
            safetyWarning = "Mantén el cuello relajado mirando al piso en todo momento.",
            videoResName = "vid_back_prone_yw_raises"
        ),

        // RESISTENCIA
        ExerciseEntity(
            code = "endurance_march_high_knees",
            name = "Marcha en el Sitio",
            category = "RESISTENCIA",
            explanation = "Eleva el pulso cardíaco progresivamente mejorando la capacidad aeróbica sin impacto.",
            instructions = "1. Inicia marchando en el lugar balanceando ambos brazos.\n2. Eleva las rodillas a la altura de la cadera alternando piernas.\n3. Aterriza suave apoyando primero la parte delantera del pie.\n4. Mantén un ritmo constante y respiración nasal o fluida.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 120,
            recommendedReps = "3 intervalos x 40 seg",
            restSeconds = 30,
            illustrationKey = "cardio",
            safetyWarning = "Ajusta la altura de rodillas y velocidad según tu nivel respiratorio actual.",
            videoResName = "vid_endurance_march_high_knees"
        ),
        ExerciseEntity(
            code = "endurance_brisk_walk",
            name = "Caminata Activa Guiada",
            category = "RESISTENCIA",
            explanation = "Caminata con postura erguida y braceo enérgico ideal para sumar pasos reales y resistencia.",
            instructions = "1. Camina con pasos ágiles apoyando talón y empujando con la punta.\n2. Dobla codos a 90° acompañando cada paso con el brazo opuesto.\n3. Mantén mirada al frente y hombros relajados.\n4. Conserva un ritmo en el que aún puedas hablar sin ahogarte.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 180,
            recommendedReps = "3 min ritmo ágil",
            restSeconds = 30,
            illustrationKey = "cardio",
            safetyWarning = "Utiliza calzado cómodo y presta atención al terreno si caminas al aire libre.",
            videoResName = "vid_endurance_brisk_walk"
        ),
        ExerciseEntity(
            code = "endurance_low_impact_aerobics",
            name = "Aeróbico de Bajo Impacto",
            category = "RESISTENCIA",
            explanation = "Pasos laterales dinámicos con elevación de brazos que cuidan tus rodillas mientras entrenas cardio.",
            instructions = "1. Da un paso amplio lateralmente llevando brazos por encima de la cabeza.\n2. Junta los pies bajando brazos y repite al lado contrario.\n3. Mantén semiflexionadas las rodillas para amortiguar suave.\n4. Disfruta un ritmo continuo sin saltos.",
            difficulty = "PRINCIPIANTE",
            durationSeconds = 120,
            recommendedReps = "3 rondas x 40 seg",
            restSeconds = 30,
            illustrationKey = "cardio",
            safetyWarning = "Asegura espacio libre a los lados para evitar tropiezos.",
            videoResName = "vid_endurance_low_impact_aerobics"
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
            illustrationKey = "cardio",
            safetyWarning = "Si sientes impacto en rodillas o tobillos, realiza la variante sin salto dando un paso lateral.",
            videoResName = "vid_endurance_jumping_jacks"
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
            illustrationKey = "plank",
            safetyWarning = "Puedes hacerlo más lento o con manos apoyadas en una silla firme para reducir exigencia.",
            videoResName = "vid_endurance_mountain_climbers"
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
            illustrationKey = "mobility",
            safetyWarning = "Sincroniza cada fase con tu respiración sin forzar el cuello.",
            videoResName = "vid_mobility_cat_cow"
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
            illustrationKey = "mobility",
            safetyWarning = "Apoya la rodilla trasera en el piso si necesitas mayor estabilidad.",
            videoResName = "vid_mobility_world_greatest_stretch"
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
            illustrationKey = "fullbody",
            safetyWarning = "Mantén el peso equilibrado al bajar y evita encorvar la espalda.",
            videoResName = "vid_fullbody_squat_to_reach"
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
            illustrationKey = "fullbody",
            safetyWarning = "Realiza cada paso atrás con control evitando dejar caer bruscamente la zona lumbar.",
            videoResName = "vid_fullbody_step_back_burpee"
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
            illustrationKey = "fullbody",
            safetyWarning = "Flexiona ligeramente las rodillas si la parte posterior de las piernas tira demasiado.",
            videoResName = "vid_fullbody_inchworm_walkout"
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
            exerciseCodesCsv = "warmup_joint_mobility,chest_wall_pushups,legs_bodyweight_squat,abs_dead_bug,recovery_breathing_stretch"
        ),
        RoutineEntity(
            code = "routine_endurance_flow",
            name = "Circuito Resistencia Activa",
            description = "Eleva tu capacidad aeróbica y resistencia muscular con transiciones claras y pausas medidas.",
            difficulty = "INTERMEDIO",
            estimatedMinutes = 20,
            focusGoal = "RESISTENCIA",
            exerciseCodesCsv = "warmup_general_mobility,endurance_march_high_knees,endurance_low_impact_aerobics,endurance_jumping_jacks,abs_plank_hold,recovery_breathing_stretch"
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
            exerciseCodesCsv = "warmup_joint_mobility,back_mobility,mobility_cat_cow,back_bird_dog,legs_leg_mobility,recovery_breathing_stretch"
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
