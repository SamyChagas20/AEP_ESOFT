const banco = db.getSiblingDB("ecohorta");

print("\n== 1) Plantas que podem ser plantadas em novembro (mês 11), incluindo janelas que cruzam o ano ==");
banco.plantas.find({
  janelasPlantio: { $elemMatch: { $or: [
    { $expr: { $and: [{ $lte: ["$mesInicio", "$mesFim"] }, { $lte: ["$mesInicio", 11] }, { $gte: ["$mesFim", 11] }] } },
    { $expr: { $and: [{ $gt:  ["$mesInicio", "$mesFim"] }, { $or: [{ $lte: [11, "$mesFim"] }, { $gte: [11, "$mesInicio"] }] }] } }
  ] } }
}, { nome: 1, _id: 0 }).forEach(printjson);

print("\n== 2) Plantios com os dados da planta (junção $lookup plantios -> plantas) ==");
banco.plantios.aggregate([
  { $lookup: { from: "plantas", localField: "plantaId", foreignField: "_id", as: "planta" } },
  { $unwind: "$planta" },
  { $project: { _id: 0, planta: "$planta.nome", canteiro: 1, status: 1, dataPlantio: 1,
                regas: { $size: "$regas" } } }
]).forEach(printjson);

print("\n== 3) Água total (ml) registrada por planta ==");
banco.plantios.aggregate([
  { $unwind: "$regas" },
  { $lookup: { from: "plantas", localField: "plantaId", foreignField: "_id", as: "planta" } },
  { $unwind: "$planta" },
  { $group: { _id: "$planta.nome", totalMl: { $sum: "$regas.quantidadeMl" }, regas: { $sum: 1 } } },
  { $sort: { totalMl: -1 } }
]).forEach(printjson);

print("\n== 4) Hortas de cada usuário, com quantidade de canteiros e plantios ==");
banco.usuarios.aggregate([
  { $lookup: { from: "hortas", localField: "_id", foreignField: "usuarioId", as: "hortas" } },
  { $unwind: "$hortas" },
  { $lookup: { from: "plantios", localField: "hortas._id", foreignField: "hortaId", as: "plantios" } },
  { $project: { _id: 0, usuario: "$nome", horta: "$hortas.nome",
                canteiros: { $size: "$hortas.canteiros" }, plantios: { $size: "$plantios" } } }
]).forEach(printjson);

print("\n== 5) Plantios ativos cuja última rega foi há mais de 2 dias ==");
const limite = new Date(Date.now() - 2 * 24 * 3600 * 1000);
banco.plantios.aggregate([
  { $match: { status: "ATIVO" } },
  { $addFields: { ultimaRega: { $max: "$regas.data" } } },
  { $match: { ultimaRega: { $lt: limite } } },
  { $project: { _id: 0, canteiro: 1, ultimaRega: 1 } }
]).forEach(printjson);

print("\n== 6) Plantios por status (mesma agregação usada pelo app) ==");
banco.plantios.aggregate([{ $group: { _id: "$status", total: { $sum: 1 } } }]).forEach(printjson);

print("\n== 7) Estações com o melhor mês de cada uma ==");
banco.tempo.find({}, { _id: 0, nome: 1, melhorMes: 1, justificativaMelhorMes: 1 }).sort({ ordem: 1 }).forEach(printjson);

print("\n== 8) Quantas plantas podem ser plantadas em cada estação (junção tempo -> plantas pelo código) ==");
banco.tempo.aggregate([
  { $lookup: { from: "plantas", localField: "codigo", foreignField: "estacoes", as: "plantas" } },
  { $project: { _id: 0, estacao: "$nome", melhorMes: 1, plantas: { $size: "$plantas" } } },
  { $sort: { ordem: 1 } }
]).forEach(printjson);

print("\n== 9) Plantas ideais para apartamento que precisam de pouca luz ==");
banco.plantas.find({ "apartamento.nivel": "IDEAL", nivelLuz: "MINIMA" }, { _id: 0, nome: 1, categoria: 1 })
  .sort({ nomeBusca: 1 }).forEach(printjson);

print("\n== 10) Comestíveis ideais para apartamento com luz média (meio dia de sol) ==");
banco.plantas.find({ "apartamento.nivel": "IDEAL", nivelLuz: "MEDIA", comestivel: true },
  { _id: 0, nome: 1, horasSolMinimas: 1 }).sort({ nomeBusca: 1 }).limit(15).forEach(printjson);

print("\n== 11) Plantas que podem ser plantadas no inverno e na primavera ==");
banco.plantas.find({ estacoes: { $all: ["INVERNO", "PRIMAVERA"] } }, { _id: 0, nome: 1, estacoes: 1 })
  .sort({ nomeBusca: 1 }).limit(10).forEach(printjson);

print("\n== 12) Plantas perigosas para gatos, da mais grave para a menos grave ==");
banco.saude.aggregate([
  { $match: { codigo: "GATOS" } },
  { $unwind: "$evitar" },
  { $project: { _id: 0, planta: "$evitar.plantaNome", gravidade: "$evitar.gravidade", motivo: "$evitar.motivo" } },
  { $addFields: { peso: { $switch: { branches: [
      { case: { $eq: ["$gravidade", "GRAVE"] }, then: 3 },
      { case: { $eq: ["$gravidade", "MODERADA"] }, then: 2 } ], default: 1 } } } },
  { $sort: { peso: -1, planta: 1 } },
  { $limit: 15 }
]).forEach(printjson);

print("\n== 13) Ficha de saúde de uma planta (Espinafre): o que cada registro diz sobre ela ==");
const espinafre = banco.plantas.findOne({ nomeBusca: "espinafre" });
banco.saude.aggregate([
  { $match: { $or: [{ "evitar.plantaId": espinafre._id }, { "recomendadas.plantaId": espinafre._id }] } },
  { $project: { _id: 0, registro: "$nome", tipo: 1,
      evitar: { $filter: { input: "$evitar", cond: { $eq: ["$$this.plantaId", espinafre._id] } } },
      recomendada: { $filter: { input: "$recomendadas", cond: { $eq: ["$$this.plantaId", espinafre._id] } } } } }
]).forEach(printjson);

print("\n== 14) O que evitar e o que escolher para quem tem doença renal ==");
banco.saude.aggregate([
  { $match: { codigo: "DOENCA_RENAL" } },
  { $project: { _id: 0, evitar: "$evitar.plantaNome", recomendadas: "$recomendadas.plantaNome" } }
]).forEach(printjson);

print("\n== 15) Plantas que são tóxicas para gatos E para cães (interseção das listas) ==");
const gatos = banco.saude.findOne({ codigo: "GATOS" }).evitar.map(i => i.plantaNome);
const caes = banco.saude.findOne({ codigo: "CAES" }).evitar.map(i => i.plantaNome);
printjson(gatos.filter(n => caes.includes(n)).sort());

print("\n== 16) Plantas ornamentais (não comestíveis) seguras para gatos e cães ==");
const seguras = banco.saude.findOne({ codigo: "GATOS" }).recomendadas.map(i => i.plantaId);
banco.plantas.find({ _id: { $in: seguras }, comestivel: false }, { _id: 0, nome: 1, nivelLuz: 1, "apartamento.nivel": 1 })
  .sort({ nomeBusca: 1 }).forEach(printjson);
