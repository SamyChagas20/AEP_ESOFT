# Fontes, avisos e como ampliar o catálogo

## ⚠️ Aviso importante sobre saúde
As informações das coleções `saude` (toxicidade, comorbidades) e das orientações das plantas são **educativas**.
Elas **não substituem** a avaliação de um médico, nutricionista ou veterinário.

- **Animal que comeu uma planta suspeita:** procure um veterinário imediatamente, informando o nome da planta.
  (No Brasil, também vale consultar o centro de informações toxicológicas da sua região.)
- **"Sem alerta" não significa "seguro".** O cadastro cobre as plantas do catálogo e os problemas mais conhecidos.
- **Condições de saúde:** os níveis `LEVE/MODERADA/GRAVE` resumem o cuidado geral. Casos individuais (medicamentos,
  fase da doença, alergias) mudam a orientação. Converse com o seu profissional de saúde.
- **Plantas ornamentais não são comestíveis.** Marcadas com `comestivel = false` no catálogo.

## Fontes consultadas (resumo)
O conteúdo foi montado a partir de referências públicas, reescrito em linguagem própria:

| Tema | Referências |
|---|---|
| Toxicidade para gatos e cães | Listas de plantas tóxicas e não tóxicas da **ASPCA** (aspca.org) e de sociedades protetoras de animais (ex.: Humane Society); guias veterinários de plantas tóxicas |
| Asma, rinite e plantas | Fundações de asma (ex.: Asthma Foundation NZ, Asthma Australia), centros de alergia e guias de plantas de baixo pólen |
| Doença renal e pedras (oxalato) | Guias de dieta com baixo oxalato (ex.: Oxalosis & Hyperoxaluria Foundation, serviços de urologia) |
| Anticoagulantes (varfarina) e vitamina K | Guias de dieta com varfarina de serviços de saúde e clínicas de anticoagulação (a regra é **constância**, não eliminação) |
| Gravidez e ervas | Guias de segurança de ervas na gravidez (ex.: Cleveland Clinic, universidades e revisões científicas) |
| Gastrite e refluxo | Guias de dieta para refluxo; os gatilhos variam de pessoa para pessoa |
| Alimentos que fazem mal crus | Informações de segurança alimentar sobre mandioca, feijões (lectinas) e plantas ricas em oxalato (taro/taioba) |
| Diabetes, hipertensão, tireoide, fígado | Orientações nutricionais gerais; onde a evidência é limitada, o texto diz "uso tradicional" ou "evidência limitada" |

> Convém revisar os dados com um profissional da área antes de uso em produção. Contribuições de revisão são bem-vindas.

## Escopo geográfico
Épocas de plantio, estações e "melhor mês" são indicativos para o **Centro-Sul do Brasil** (ex.: Paraná, São Paulo, Minas).
As estações usam meses inteiros no hemisfério sul: **primavera** set–nov, **verão** dez–fev, **outono** mar–mai, **inverno** jun–ago.
Norte e Nordeste têm períodos de chuva e seca que pedem outro calendário.

## Como ampliar o catálogo (arquivo CSV)
As plantas ficam em `src/main/resources/catalogo/plantas.csv`. Basta acrescentar uma linha e rodar o app
(ou `mvn verify`): a carga insere apenas as plantas que ainda não existem. Formato:

```
nome;científico;categoria;diasAteColheita;luz;apartamento;regaACadaDias;regaMl;janelasDePlantio;dica
Rúcula;Eruca vesicaria;FOL;40;M;I;1;200;3-9;Colher as folhas externas
```

| Coluna | Valores |
|---|---|
| `categoria` | `FOL` folhosa · `FRU` hortaliça de fruto · `RAI` raiz/tubérculo · `BUL` bulbo · `ERV` erva/tempero · `LEG` leguminosa/grão · `FRT` frutífera · `MED` medicinal · `PNC` PANC · `FLO` flor comestível · `ORN` ornamental (não comestível) |
| `luz` | `B` pouca luz · `M` luz média (meio dia de sol) · `P` sol pleno (dia todo) |
| `apartamento` | `I` ideal · `P` possível com cuidados · `N` não recomendado |
| `janelasDePlantio` | meses `inicio-fim` separados por `\|`; `9-11\|2-3`; janela que cruza o ano: `11-2`; ano todo: `1-12` |
| `dica` | texto livre (vira um cuidado de manejo); pode conter `;` |

As estações da planta e o melhor mês são **calculados** a partir das janelas. Os cuidados periódicos (adubação, poda, pragas)
vêm de um modelo por categoria (`CuidadosPadrao`). O teste `CatalogoCsvTest` valida o arquivo: nomes únicos (mesmo sem
acentos), campos válidos e cobertura de categorias, luz, apartamento e estações.

Para registrar alertas de saúde de uma planta nova, edite `SaudeInicial.java` (as plantas são citadas pelo **nome do catálogo**;
o teste `SaudeServiceTest` falha se algum nome não existir ou se uma planta se repetir na mesma lista).
