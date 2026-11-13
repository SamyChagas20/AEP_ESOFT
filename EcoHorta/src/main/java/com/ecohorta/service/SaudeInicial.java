package com.ecohorta.service;

import com.ecohorta.model.Gravidade;
import com.ecohorta.model.TipoRegistroSaude;

import java.util.ArrayList;
import java.util.List;

/**
 * Dados iniciais da coleção "saude": toxicidade para gatos, cães e pessoas, e orientações para
 * condições de saúde (comorbidades). As plantas são citadas pelo NOME do catálogo e convertidas
 * em referências pelo {@link SaudeService}.
 *
 * Material educativo, compilado de fontes públicas (ASPCA, sociedades de asma e alergia, nutrição
 * clínica para dieta renal, varfarina e refluxo). Não substitui médico, nutricionista ou veterinário.
 */
public final class SaudeInicial {

    static final String AVISO_PADRAO =
            "Informação educativa. Não substitui a orientação de médico, nutricionista ou veterinário.";

    /** Planta citada em uma lista, com o motivo e (para "evitar") a gravidade. */
    public record Entrada(String planta, String motivo, Gravidade gravidade) {
    }

    /** Definição completa de um documento da coleção "saude". */
    public record Definicao(String codigo, TipoRegistroSaude tipo, String nome, String descricao,
                            String orientacao, String aviso, List<Entrada> evitar, List<Entrada> recomendadas) {
    }

    private SaudeInicial() {
    }

    // ---------- ajudantes para montar as listas ----------

    private static List<Entrada> ev(Gravidade gravidade, String motivo, String... plantas) {
        List<Entrada> lista = new ArrayList<>();
        for (String planta : plantas) {
            lista.add(new Entrada(planta, motivo, gravidade));
        }
        return lista;
    }

    private static List<Entrada> ok(String motivo, String... plantas) {
        return ev(null, motivo, plantas);
    }

    @SafeVarargs
    private static List<Entrada> juntar(List<Entrada>... listas) {
        List<Entrada> todas = new ArrayList<>();
        for (List<Entrada> lista : listas) {
            todas.addAll(lista);
        }
        return todas;
    }

    private static final String[] ALLIUMS = {"Cebola", "Cebola-roxa", "Cebola-de-cabeça-pequena", "Alho",
            "Alho-poró", "Chalota", "Cebolinha", "Cebolinha-chinesa"};
    private static final String[] TOMATES = {"Tomate", "Tomate-cereja", "Tomate-italiano"};
    private static final String[] PIMENTAS = {"Pimenta", "Pimenta-malagueta", "Pimenta-dedo-de-moça",
            "Pimenta-biquinho", "Pimenta-jalapeño", "Pimenta-habanero", "Pimenta-de-cheiro", "Pimenta-cumari"};
    private static final String[] CITRICOS = {"Limão-taiti", "Limão-siciliano", "Laranja", "Tangerina"};
    private static final String[] AROIDES_PET = {"Jiboia", "Lírio-da-paz", "Costela-de-adão", "Filodendro",
            "Antúrio", "Copo-de-leite", "Zamioculca", "Singônio", "Begônia", "Taioba", "Inhame", "Mangarito"};
    private static final String[] FEIJOES_CRUS = {"Feijão-carioca", "Feijão-preto", "Feijão-de-corda",
            "Feijão-guandu", "Fava"};
    private static final String[] CRUCIFERAS = {"Couve", "Repolho", "Repolho-roxo", "Brócolis", "Couve-flor",
            "Couve-de-bruxelas", "Mostarda", "Rúcula", "Agrião", "Nabo", "Rabanete", "Couve-chinesa", "Pak choi",
            "Mizuna", "Tatsoi", "Couve-rábano", "Raiz-forte", "Rutabaga"};

    /** Plantas consideradas seguras para gatos e cães (listas de plantas não tóxicas da ASPCA). */
    private static List<Entrada> segurasParaPets() {
        return ok("Considerada não tóxica para cães e gatos; ofereça com moderação e supervisão",
                "Manjericão", "Manjericão-roxo", "Alecrim", "Tomilho", "Sálvia", "Coentro", "Endro",
                "Capuchinha", "Girassol", "Rosa", "Amor-perfeito", "Alisso", "Zínia", "Cosmos",
                "Pepino", "Cenoura", "Abobrinha", "Morango", "Mirtilo", "Framboesa", "Amora", "Banana",
                "Samambaia", "Clorofito", "Palmeira-areca", "Calathea", "Maranta", "Peperômia", "Bromélia",
                "Orquídea", "Violeta-africana", "Echeveria", "Haworthia");
    }

    public static List<Definicao> registros() {
        return List.of(gatos(), caes(), pessoas(), respiratorio(), alergiaPolen(), hipertensao(), diabetes(),
                doencaRenal(), anticoagulante(), gravidez(), refluxo(), tireoide(), hepatica());
    }

    // ---------- animais ----------

    private static List<Entrada> toxicasParaPets(boolean gatos) {
        return juntar(
                ev(Gravidade.GRAVE, "Cebola, alho e parentes (Allium) danificam as hemácias e causam anemia"
                        + (gatos ? "; gatos são ainda mais sensíveis" : "") + ", crus, cozidos ou em pó", ALLIUMS),
                ev(Gravidade.GRAVE, "Cica: toda a planta, sobretudo as sementes, pode causar falência do fígado", "Cica"),
                ev(Gravidade.GRAVE, "Sementes com ricina, toxina potente (mamona)", "Mamona"),
                ev(Gravidade.GRAVE, "Todas as partes afetam o coração (espirradeira)", "Espirradeira"),
                ev(Gravidade.GRAVE, "Grayanotoxinas causam vômito, fraqueza e problemas no coração", "Azaleia"),
                gatos
                        ? ev(Gravidade.GRAVE, "Qualquer parte do lírio, até a água do vaso, pode causar insuficiência renal aguda em gatos", "Lírio")
                        : ev(Gravidade.LEVE, "O lírio pode causar vômito e salivação em cães", "Lírio"),
                gatos ? List.<Entrada>of()
                        : ev(Gravidade.GRAVE, "Uvas causam insuficiência renal aguda em cães", "Uva"),
                ev(Gravidade.MODERADA, "Folhas, caules e frutos verdes têm solanina/tomatina: vômito, diarreia e fraqueza", TOMATES),
                ev(Gravidade.MODERADA, "Partes verdes e brotos de batata têm solanina", "Batata"),
                ev(Gravidade.LEVE, "Folhas e caules da berinjela têm solaninas", "Berinjela"),
                ev(Gravidade.MODERADA, "Saponinas e antraquinonas causam vômito, diarreia e letargia", "Babosa"),
                ev(Gravidade.MODERADA, "Compostos que afetam o coração (bufadienolídeos)", "Kalanchoe"),
                ev(Gravidade.MODERADA, "Oxalato insolúvel irrita a boca e a garganta: baba, vômito, dificuldade para engolir",
                        "Comigo-ninguém-pode"),
                ev(Gravidade.MODERADA, "Saponinas causam vômito, salivação e diarreia", "Hera"),
                ev(Gravidade.MODERADA, "Partes verdes e frutos crus contêm glicosídeos cianogênicos", "Sabugueiro"),
                ev(Gravidade.LEVE, "Oxalato insolúvel irrita a boca e o estômago", AROIDES_PET),
                ev(Gravidade.LEVE, "Óleos essenciais causam vômito, diarreia e baba",
                        "Hortelã", "Hortelã-pimenta", "Orégano", "Manjerona", "Estragão", "Lavanda", "Camomila",
                        "Capim-limão", "Louro", "Poejo", "Citronela", "Gerânio-perfumado", "Salsinha", "Arruda",
                        "Erva-de-santa-maria", "Confrei"),
                ev(Gravidade.LEVE, "Óleos e psoralenos da casca, folhas e sementes irritam o estômago", CITRICOS),
                ev(Gravidade.LEVE, "O látex irrita a pele e o estômago", "Figo", "Fícus-benjamim", "Fícus-lira", "Seringueira"),
                ev(Gravidade.LEVE, "A persina e a gordura podem causar vômito e diarreia; o caroço pode obstruir", "Abacate"),
                ev(Gravidade.LEVE, "A capsaicina irrita a boca e o estômago (não é veneno, mas dói)", PIMENTAS),
                ev(Gravidade.LEVE, "Saponinas e outros compostos causam vômito e diarreia",
                        "Dracena", "Bambu-da-sorte", "Espada-de-são-jorge", "Planta-jade", "Crisântemo", "Margarida",
                        "Dália", "Cravo-de-defunto", "Trapoeraba", "Hortênsia"));
    }

    private static Definicao gatos() {
        return new Definicao("GATOS", TipoRegistroSaude.ANIMAL, "Gatos",
                "Gatos mordiscam folhas e têm dificuldade para eliminar certas toxinas. Os pontos mais perigosos "
                        + "são lírios, cebola/alho e plantas ornamentais comuns em apartamentos.",
                "Mantenha as plantas perigosas fora do alcance, prefira as seguras e, se o gato comer alguma, "
                        + "ligue para o veterinário imediatamente, levando o nome da planta.",
                AVISO_PADRAO, toxicasParaPets(true), segurasParaPets());
    }

    private static Definicao caes() {
        List<Entrada> evitar = juntar(toxicasParaPets(false),
                ev(Gravidade.MODERADA, "Crus contêm lectinas que causam vômito e diarreia intensos", FEIJOES_CRUS),
                ev(Gravidade.MODERADA, "Caroço e folhas têm amigdalina (libera cianeto) e o caroço obstrui", "Pêssego"),
                ev(Gravidade.LEVE, "A espiga (sabugo) pode obstruir o intestino", "Milho-verde", "Milho-pipoca"),
                ev(Gravidade.LEVE, "Caroço ou sementes podem obstruir o intestino", "Manga", "Caqui"));
        return new Definicao("CAES", TipoRegistroSaude.ANIMAL, "Cães",
                "Cães comem quase tudo no quintal. Uvas, cebola/alho, partes verdes do tomate e ornamentais "
                        + "como azaleia e cica estão entre as principais causas de intoxicação.",
                "Proteja a horta com cerca baixa ou canteiro suspenso, evite adubos que atraem o cão (farinha de "
                        + "osso, esterco fresco) e procure o veterinário ao menor sinal de ingestão.",
                AVISO_PADRAO, evitar, segurasParaPets());
    }

    private static Definicao pessoas() {
        return new Definicao("PESSOAS", TipoRegistroSaude.PESSOA, "Pessoas (consumo)",
                "Algumas plantas fazem mal quando comidas cruas, mal preparadas, em partes erradas ou em excesso.",
                "Cozinhe sempre mandioca, feijão, taioba, inhame e chaya; não coma folhas e frutos verdes de tomate e "
                        + "batata; nunca consuma plantas ornamentais; em dúvida, não coma.",
                AVISO_PADRAO,
                juntar(
                        ev(Gravidade.GRAVE, "Todas as partes (sementes especialmente) são muito tóxicas", "Mamona", "Espirradeira", "Azaleia", "Cica"),
                        ev(Gravidade.GRAVE, "Crua ou mal processada libera cianeto (variedades bravas); só consumir cozida e bem preparada", "Mandioca"),
                        ev(Gravidade.GRAVE, "Uso interno associado a lesão do fígado (alcaloides pirrolizidínicos)", "Confrei"),
                        ev(Gravidade.MODERADA, "Alcaloides pirrolizidínicos podem lesar o fígado em uso frequente", "Borragem"),
                        ev(Gravidade.MODERADA, "Crus ou mal cozidos contêm lectinas: vômito e diarreia intensos", FEIJOES_CRUS),
                        ev(Gravidade.MODERADA, "Cristais de oxalato irritam boca e garganta; cozinhar sempre", "Taioba", "Inhame", "Mangarito", "Cará"),
                        ev(Gravidade.MODERADA, "Crua tem glicosídeos cianogênicos; ferver bem antes de comer", "Chaya"),
                        ev(Gravidade.MODERADA, "Partes verdes e brotos têm solanina (náusea, dor de cabeça)", "Batata"),
                        ev(Gravidade.MODERADA, "Pode ser tóxica ao fígado e causar queimadura na pele com sol", "Arruda"),
                        ev(Gravidade.MODERADA, "Contém pulegona, tóxica ao fígado em doses altas", "Poejo"),
                        ev(Gravidade.MODERADA, "Contém ascaridol, tóxico em doses altas", "Erva-de-santa-maria"),
                        ev(Gravidade.MODERADA, "Contém tujona, que pode ser neurotóxica", "Losna"),
                        ev(Gravidade.MODERADA, "Partes verdes e frutos crus causam náusea e vômito", "Sabugueiro"),
                        ev(Gravidade.MODERADA, "Ornamental não comestível: oxalato e outros compostos tóxicos", "Comigo-ninguém-pode", "Hortênsia", "Copo-de-leite"),
                        ev(Gravidade.LEVE, "Folhas e frutos verdes têm solanina; coma apenas o fruto maduro", TOMATES),
                        ev(Gravidade.LEVE, "O látex (casca) é laxante forte; só o gel interno é usado", "Babosa"),
                        ev(Gravidade.LEVE, "Queimação intensa na boca e na pele; use luvas", "Pimenta-habanero"),
                        ev(Gravidade.LEVE, "Casca e fruto verdes podem conter compostos cianogênicos; consuma maduro", "Maracujá"),
                        ev(Gravidade.LEVE, "Coma cozida: crua causa desconforto", "Vagem", "Soja-edamame"),
                        ev(Gravidade.LEVE, "O látex irrita a pele e a boca", "Figo"),
                        ev(Gravidade.LEVE, "Ornamental não comestível; pode irritar a boca", "Lírio-da-paz", "Antúrio", "Hera")),
                List.of());
    }

    // ---------- condições de saúde ----------

    private static Definicao respiratorio() {
        return new Definicao("RESPIRATORIO", TipoRegistroSaude.COMORBIDADE,
                "Problemas respiratórios (asma, bronquite, DPOC)",
                "Vias aéreas sensíveis reagem a pólen, perfumes fortes, mofo e poeira. Escolha plantas de baixo "
                        + "pólen e fáceis de limpar, e evite substrato encharcado.",
                "Retire flores secas, limpe as folhas com pano úmido, não deixe água parada no prato (mofo), lave "
                        + "as mãos depois de mexer na terra e evite perfumes fortes em ambientes fechados.",
                "Informação educativa. Em crise de asma ou falta de ar, procure atendimento médico.",
                juntar(
                        ev(Gravidade.MODERADA, "Muito pólen (família das margaridas), que pode desencadear sintomas",
                                "Girassol", "Crisântemo", "Margarida", "Cravo-de-defunto", "Calêndula", "Camomila"),
                        ev(Gravidade.MODERADA, "Perfume intenso, que pode irritar as vias aéreas em ambientes fechados",
                                "Lírio", "Lavanda", "Gerânio-perfumado"),
                        ev(Gravidade.LEVE, "O látex e a poeira acumulada podem causar reação", "Fícus-benjamim"),
                        ev(Gravidade.LEVE, "O vapor da pimenta ao manusear ou cozinhar irrita as vias aéreas", PIMENTAS),
                        ev(Gravidade.LEVE, "O mentol concentrado pode irritar vias aéreas sensíveis", "Hortelã-pimenta")),
                juntar(
                        ok("Folhagem de pouco pólen e fácil de limpar",
                                "Jiboia", "Dracena", "Palmeira-areca", "Filodendro", "Espada-de-são-jorge", "Clorofito"),
                        ok("Flor de baixo pólen em comparação às margaridas", "Amor-perfeito", "Zínia"),
                        ok("Erva de aroma suave", "Salsinha", "Tomilho"),
                        ok("Folhosa colhida antes de florir quase não libera pólen", "Alface", "Rúcula", "Couve", "Espinafre")));
    }

    private static Definicao alergiaPolen() {
        return new Definicao("ALERGIA_POLEN", TipoRegistroSaude.COMORBIDADE,
                "Alergia a pólen (rinite alérgica e síndrome pólen-alimento)",
                "Quem é alérgico a pólens de ambrosia, artemísia ou bétula pode reagir a plantas aparentadas, "
                        + "inclusive comestíveis (formigamento na boca ao comer).",
                "Se notar coceira na boca ou garganta ao comer crus, prefira cozidos e converse com um alergista.",
                AVISO_PADRAO,
                juntar(
                        ev(Gravidade.MODERADA, "Mesma família da ambrosia e da artemísia (Asteraceae): pólen e reação cruzada",
                                "Camomila", "Girassol", "Crisântemo", "Calêndula", "Margarida"),
                        ev(Gravidade.LEVE, "Parente das asteráceas: possível reação oral cruzada quando crua",
                                "Chicória", "Endívia", "Escarola", "Radicchio", "Alcachofra", "Almeirão"),
                        ev(Gravidade.LEVE, "Síndrome aipo-artemísia-especiarias: possível reação oral cruzada",
                                "Salsão", "Cenoura", "Salsinha", "Funcho"),
                        ev(Gravidade.LEVE, "Possível reação oral cruzada em alérgicos a ambrosia",
                                "Melão", "Melancia", "Banana")),
                ok("Flor ou folhagem de baixo pólen",
                        "Amor-perfeito", "Zínia", "Jiboia", "Dracena", "Palmeira-areca", "Filodendro"));
    }

    private static Definicao hipertensao() {
        return new Definicao("HIPERTENSAO", TipoRegistroSaude.COMORBIDADE, "Pressão alta (hipertensão)",
                "O controle da pressão se apoia em menos sal, mais potássio e fibras. Ervas frescas ajudam a "
                        + "temperar sem sal.",
                "Troque parte do sal por ervas e temperos, aumente hortaliças e frutas e converse com o médico "
                        + "antes de usar chás medicinais regularmente.",
                AVISO_PADRAO,
                ev(Gravidade.MODERADA, "Interage com vários medicamentos e pode reduzir o efeito de alguns remédios do coração e da pressão",
                        "Erva-de-são-joão"),
                juntar(
                        ok("Ervas e temperos substituem parte do sal",
                                "Manjericão", "Orégano", "Alecrim", "Coentro", "Salsinha", "Cebolinha", "Tomilho"),
                        ok("Uso tradicional; evidência modesta de redução leve da pressão", "Alho"),
                        ok("Rica em nitratos, que ajudam o relaxamento dos vasos", "Beterraba"),
                        ok("O chá do cálice reduziu levemente a pressão em estudos; cuidado se já usa anti-hipertensivo", "Vinagreira"),
                        ok("Fonte de potássio, que ajuda a equilibrar o sódio", "Couve", "Banana", "Tomate", "Abóbora-cabotiá"),
                        ok("Uso tradicional; evidência limitada", "Salsão")));
    }

    private static Definicao diabetes() {
        return new Definicao("DIABETES", TipoRegistroSaude.COMORBIDADE, "Diabetes",
                "Priorize hortaliças de baixo índice glicêmico e ricas em fibras; modere amidos e frutas muito doces.",
                "Combine as porções com o plano alimentar do nutricionista e observe a glicemia após novos alimentos.",
                AVISO_PADRAO,
                ev(Gravidade.LEVE, "Amido ou açúcar elevam a glicemia rapidamente: moderar a porção",
                        "Batata", "Mandioca", "Batata-doce", "Milho-verde", "Banana", "Uva", "Manga", "Melancia",
                        "Abacaxi", "Figo", "Caqui"),
                juntar(
                        ok("Pouco amido e muita fibra: baixo impacto na glicemia",
                                "Couve", "Brócolis", "Couve-flor", "Rúcula", "Alface", "Espinafre", "Pepino", "Abobrinha",
                                "Berinjela", "Jiló", "Quiabo", "Chuchu", "Vagem"),
                        ok("Fibras e proteínas ajudam o controle da glicemia (porção moderada)",
                                "Feijão-carioca", "Feijão-preto", "Grão-de-bico", "Lentilha"),
                        ok("Frutas vermelhas têm menor impacto glicêmico", "Morango", "Framboesa", "Mirtilo", "Amora"),
                        ok("Folha adoçante sem calorias", "Stevia")));
    }

    private static Definicao doencaRenal() {
        return new Definicao("DOENCA_RENAL", TipoRegistroSaude.COMORBIDADE, "Doença renal e pedras nos rins",
                "Quem tem pedras de oxalato deve limitar alimentos ricos em oxalato; na doença renal crônica, "
                        + "também importam o potássio e o fósforo.",
                "Beba bastante água, siga a dieta do nutricionista e prefira hortaliças de baixo oxalato.",
                AVISO_PADRAO,
                juntar(
                        ev(Gravidade.GRAVE, "Contém uma neurotoxina que se acumula quando os rins não funcionam bem", "Carambola"),
                        ev(Gravidade.GRAVE, "Muito rico em oxalato", "Espinafre", "Acelga", "Azedinha", "Beldroega"),
                        ev(Gravidade.MODERADA, "Rico em oxalato", "Beterraba", "Quiabo", "Batata-doce", "Taioba", "Amendoim"),
                        ev(Gravidade.LEVE, "Oxalato moderado; moderar a porção", "Salsinha", "Figo"),
                        ev(Gravidade.LEVE, "Rico em potássio; limitar na doença renal crônica avançada", "Tomate", "Banana", "Batata")),
                juntar(
                        ok("Baixo oxalato e pouco potássio",
                                "Couve-flor", "Repolho", "Pepino", "Alface-americana", "Cebola", "Alho", "Rabanete"),
                        ok("Fruta de baixo oxalato", "Mamão", "Abacaxi", "Morango"),
                        ok("Erva de baixíssimo oxalato", "Tomilho")));
    }

    private static Definicao anticoagulante() {
        return new Definicao("ANTICOAGULANTE", TipoRegistroSaude.COMORBIDADE, "Uso de anticoagulante (varfarina)",
                "A vitamina K compete com a varfarina. O segredo não é cortar as verduras, e sim manter uma "
                        + "quantidade parecida todos os dias.",
                "Não mude de repente a quantidade de folhas verdes escuras e avise o médico antes de começar "
                        + "chás, suplementos ou ervas medicinais.",
                AVISO_PADRAO,
                juntar(
                        ev(Gravidade.MODERADA, "Muito rica em vitamina K: manter porções constantes",
                                "Couve", "Espinafre", "Brócolis", "Couve-de-bruxelas", "Acelga", "Mostarda", "Agrião",
                                "Salsinha", "Chicória", "Endívia"),
                        ev(Gravidade.LEVE, "Boa quantidade de vitamina K: manter regularidade",
                                "Cebolinha", "Rúcula", "Repolho", "Coentro", "Alface-crespa", "Alface-romana"),
                        ev(Gravidade.MODERADA, "Interage com anticoagulantes e outros remédios", "Erva-de-são-joão"),
                        ev(Gravidade.MODERADA, "Contém cumarina, que pode somar efeito ao anticoagulante", "Guaco"),
                        ev(Gravidade.LEVE, "Em doses altas (chás, suplementos) pode aumentar o risco de sangramento",
                                "Gengibre", "Alho", "Cúrcuma", "Camomila")),
                ok("Pouca vitamina K, de menor interferência",
                        "Cenoura", "Tomate", "Batata", "Pepino", "Abobrinha", "Cebola", "Milho-verde", "Banana",
                        "Batata-doce", "Berinjela"));
    }

    private static Definicao gravidez() {
        return new Definicao("GRAVIDEZ_LACTACAO", TipoRegistroSaude.COMORBIDADE, "Gravidez e amamentação",
                "Muitas ervas medicinais não têm segurança comprovada na gravidez; algumas podem estimular o útero "
                        + "ou afetar o bebê e o leite.",
                "Use ervas só como tempero, evite chás medicinais sem orientação e lave muito bem folhas e frutas "
                        + "consumidas cruas.",
                AVISO_PADRAO,
                juntar(
                        ev(Gravidade.GRAVE, "Pode estimular contrações e causar aborto", "Arruda", "Poejo", "Losna",
                                "Erva-de-santa-maria"),
                        ev(Gravidade.GRAVE, "Uso interno associado a lesão do fígado", "Confrei"),
                        ev(Gravidade.MODERADA, "Mamão verde ou o látex pode estimular contrações", "Mamão"),
                        ev(Gravidade.MODERADA, "Segurança não comprovada; evitar uso interno",
                                "Mil-folhas", "Babosa", "Borragem", "Hissopo", "Erva-de-são-joão"),
                        ev(Gravidade.MODERADA, "Chá ou doses medicinais podem estimular o útero ou reduzir o leite", "Sálvia"),
                        ev(Gravidade.LEVE, "Como tempero é ok; evitar chás concentrados e óleos",
                                "Orégano", "Hortelã-pimenta", "Salsinha", "Manjerona", "Alecrim"),
                        ev(Gravidade.LEVE, "Evitar chás em grande quantidade",
                                "Camomila", "Erva-cidreira", "Guaco", "Valeriana")),
                juntar(
                        ok("Fonte de folato e ferro", "Couve", "Brócolis", "Feijão-preto", "Feijão-carioca",
                                "Lentilha", "Grão-de-bico", "Laranja", "Cenoura"),
                        ok("O gengibre em quantidade culinária pode aliviar náuseas", "Gengibre")));
    }

    private static Definicao refluxo() {
        return new Definicao("GASTRITE_REFLUXO", TipoRegistroSaude.COMORBIDADE, "Gastrite e refluxo",
                "Alimentos ácidos, picantes ou muito aromáticos costumam piorar os sintomas, mas os gatilhos "
                        + "variam de pessoa para pessoa.",
                "Observe quais alimentos pioram os seus sintomas, faça refeições menores e prefira preparos cozidos.",
                AVISO_PADRAO,
                juntar(
                        ev(Gravidade.MODERADA, "A capsaicina irrita o estômago e o esôfago", PIMENTAS),
                        ev(Gravidade.MODERADA, "Ácido; piora o refluxo em muitas pessoas", TOMATES),
                        ev(Gravidade.MODERADA, "A menta relaxa o esfíncter do esôfago e favorece o refluxo", "Hortelã-pimenta"),
                        ev(Gravidade.LEVE, "A hortelã pode piorar o refluxo em quem é sensível", "Hortelã"),
                        ev(Gravidade.LEVE, "Crus costumam ser gatilho de azia", "Cebola", "Cebola-roxa", "Alho"),
                        ev(Gravidade.LEVE, "Frutas cítricas e ácidas podem irritar", "Limão-taiti", "Limão-siciliano",
                                "Laranja", "Tangerina", "Abacaxi")),
                juntar(
                        ok("Tradicional para náuseas e digestão, em pouca quantidade", "Gengibre"),
                        ok("Chás suaves de uso tradicional para o estômago", "Camomila", "Funcho", "Erva-doce", "Erva-cidreira"),
                        ok("Frutas pouco ácidas", "Banana", "Mamão", "Melão", "Melancia"),
                        ok("Hortaliças suaves, de preferência cozidas", "Couve-flor", "Pepino", "Alface", "Salsão",
                                "Abobrinha", "Abóbora-cabotiá", "Cenoura", "Batata", "Chuchu")));
    }

    private static Definicao tireoide() {
        return new Definicao("TIREOIDE", TipoRegistroSaude.COMORBIDADE, "Problemas de tireoide (hipotireoidismo)",
                "Crucíferas cruas e em grande quantidade contêm goitrógenos que podem atrapalhar a tireoide, "
                        + "principalmente quando falta iodo. Cozinhar reduz bastante o efeito.",
                "Prefira as crucíferas cozidas, varie a alimentação e tome o remédio da tireoide longe de alimentos "
                        + "(e de soja), como o médico orientar.",
                "Informação educativa. O efeito é relevante só em consumo cru e em grandes quantidades; siga o seu endocrinologista.",
                juntar(
                        ev(Gravidade.LEVE, "Crucífera crua em grande quantidade pode interferir na tireoide; cozinhar reduz o efeito", CRUCIFERAS),
                        ev(Gravidade.LEVE, "Contém compostos que liberam tiocianato; consumir cozida e variada", "Mandioca"),
                        ev(Gravidade.LEVE, "As isoflavonas podem reduzir a absorção do remédio; separar os horários", "Soja-edamame")),
                ok("Sem goitrógenos relevantes", "Alface", "Cenoura", "Tomate", "Pepino", "Abobrinha", "Pimentão"));
    }

    private static Definicao hepatica() {
        return new Definicao("HEPATICA", TipoRegistroSaude.COMORBIDADE, "Doenças do fígado",
                "O fígado processa as substâncias das plantas: ervas medicinais e chás concentrados podem sobrecarregá-lo.",
                "Evite chás medicinais e suplementos de ervas sem orientação médica.",
                AVISO_PADRAO,
                juntar(
                        ev(Gravidade.GRAVE, "Alcaloides pirrolizidínicos causam lesão no fígado", "Confrei"),
                        ev(Gravidade.GRAVE, "A pulegona é tóxica ao fígado", "Poejo"),
                        ev(Gravidade.MODERADA, "Alcaloides pirrolizidínicos em uso frequente", "Borragem"),
                        ev(Gravidade.MODERADA, "Pode ser tóxica ao fígado", "Arruda", "Erva-de-santa-maria", "Losna"),
                        ev(Gravidade.LEVE, "Como tempero é seguro, mas suplementos concentrados já foram ligados a lesão hepática", "Cúrcuma")),
                ok("Uso tradicional para a digestão; evidência limitada, converse com o médico",
                        "Alcachofra", "Dente-de-leão"));
    }
}
