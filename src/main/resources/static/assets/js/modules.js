/* MEDSiNC Web — módulos demonstrativos. Não utilizar dados reais. */
(() => {
"use strict";
const KEY="medsinc_web_modules_v1";
const PATIENTS_KEY="medsinc-web-pacientes-v2";
const menus=[
 ["dashboard","Painel","../dashboard.html"],["pacientes","Pacientes","cadastro-paciente.html"],["consultas","Consultas","modulos.html?mod=consultas"],["estoque","Medicamentos e estoque","modulos.html?mod=estoque"],["historico","Histórico médico","modulos.html?mod=historico"],["usuarios","Usuários","modulos.html?mod=usuarios"],["relatorios","Relatórios","modulos.html?mod=relatorios"],["backups","Backups","modulos.html?mod=backups"]
];
const configs={
 consultas:{title:"Consultas",desc:"Agende e acompanhe os atendimentos.",fields:[["paciente","Paciente","text",true],["profissional","Profissional","text",true],["data","Data","date",true],["hora","Horário","time",true],["status","Status","select",true,["Agendada","Realizada","Cancelada"]]],columns:["Paciente","Profissional","Data","Horário","Status"]},
 estoque:{title:"Medicamentos e estoque",desc:"Controle itens, quantidades e níveis mínimos.",fields:[["nome","Medicamento / item","text",true],["quantidade","Quantidade atual","number",true],["unidade","Unidade de medida","text",true],["minimo","Estoque mínimo","number",true]],columns:["Item","Quantidade","Unidade","Mínimo","Situação"]},
 historico:{title:"Histórico médico",desc:"Consulte e registre anotações demonstrativas por paciente.",fields:[["paciente","Paciente","text",true],["profissional","Profissional responsável","text",true],["data","Data do registro","date",true],["descricao","Descrição","textarea",true]],columns:["Paciente","Profissional","Data","Descrição"]},
 usuarios:{title:"Usuários e perfis",desc:"Cadastro demonstrativo de usuários. Não armazene senhas nesta interface.",fields:[["nome","Nome","text",true],["email","E-mail","email",true],["perfil","Perfil de acesso","select",true,["Administrador","Médico","Recepcionista","Farmacêutico","Técnico"]],["situacao","Situação","select",true,["Ativo","Inativo"]]],columns:["Nome","E-mail","Perfil","Situação"]},
 backups:{title:"Backups",desc:"Registro demonstrativo de solicitações de backup. Cópias reais exigem servidor.",fields:[["descricao","Descrição","text",true],["responsavel","Responsável","text",true]],columns:["Descrição","Responsável","Data/Hora"]},
 relatorios:{title:"Relatórios",desc:"Indicadores calculados a partir dos dados locais desta demonstração.",fields:[],columns:[]}
};
function read(){try{const data=JSON.parse(localStorage.getItem(KEY)||"{}");return data&&typeof data==="object"&&!Array.isArray(data)?data:{}}catch{return {}}}
function write(db){try{localStorage.setItem(KEY,JSON.stringify(db));return true}catch{return false}}
function rows(mod){const db=read();return Array.isArray(db[mod])?db[mod]:[]}
function esc(v){return String(v??"").replace(/[&<>"']/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]))}
function dateBR(v){if(!v)return "—";const [y,m,d]=v.split("-");return d+"/"+m+"/"+y}
function init(){
 const nav=document.getElementById("module-nav"),content=document.getElementById("module-content");if(!nav||!content)return;
 const mod=new URLSearchParams(location.search).get("mod")||"consultas",cfg=configs[mod];
 nav.innerHTML=menus.map(([id,label,url])=>'<a class="nav-link '+(id===mod?'active':'')+'" href="'+url+'"><span aria-hidden="true">'+({dashboard:"▦",pacientes:"♙",consultas:"▣",estoque:"＋",historico:"▤",usuarios:"♧",relatorios:"▥",backups:"◷"}[id])+'</span>'+label+'</a>').join("");
 if(!cfg){content.innerHTML='<p>Módulo não encontrado.</p>';return}
 document.title=cfg.title+" | MEDSiNC";document.getElementById("module-title").textContent=cfg.title;document.getElementById("module-description").textContent=cfg.desc;
 if(mod==="relatorios"){renderReports(content);return}
 if(mod==="backups"){renderBackups(content,cfg);return}
 renderCrud(content,mod,cfg);
}
function renderCrud(root,mod,cfg){
 root.innerHTML='<div class="module-toolbar"><div><h2>Novo registro</h2><p class="muted">Preencha os campos para adicionar à lista.</p></div><span class="module-count" id="module-count"></span></div><form id="module-form" class="module-form"><div class="module-fields">'+cfg.fields.map(([id,label,type,required,opts])=>'<div class="field '+(type==="textarea"?'field-span-2':'')+'"><label for="f-'+id+'">'+label+(required?' *':'')+'</label>'+(type==="select"?'<select id="f-'+id+'" name="'+id+'" '+(required?'required':'')+'><option value="">Selecione</option>'+opts.map(o=>'<option>'+o+'</option>').join("")+'</select>':type==="textarea"?'<textarea id="f-'+id+'" name="'+id+'" rows="3" required></textarea>':'<input id="f-'+id+'" name="'+id+'" type="'+type+'" '+(required?'required':'')+(type==="number"?' min="0" step="1"':'')+'>')+'</div>').join("")+'</div><div class="form-actions"><button class="button button-primary" type="submit">Salvar registro</button></div><p class="form-feedback" id="module-feedback" role="status"></p></form><div class="module-list-head"><h2>Registros</h2><input id="module-filter" type="search" placeholder="Filtrar registros..." aria-label="Filtrar registros"></div><div class="table-wrap"><table><thead><tr><th>ID</th>'+cfg.columns.map(c=>'<th>'+c+'</th>').join("")+'<th>Ações</th></tr></thead><tbody id="module-rows"></tbody></table></div>';
 const form=root.querySelector("#module-form"),feedback=root.querySelector("#module-feedback"),filter=root.querySelector("#module-filter");
 function draw(){const term=filter.value.toLocaleLowerCase("pt-BR"),all=rows(mod),list=all.filter(r=>JSON.stringify(r).toLocaleLowerCase("pt-BR").includes(term));root.querySelector("#module-count").textContent=all.length+" registro(s)";root.querySelector("#module-rows").innerHTML=list.length?list.map(r=>'<tr><td>#'+esc(r.id)+'</td>'+cfg.fields.map(([id])=>'<td>'+esc(id==="data"?dateBR(r[id]):r[id])+'</td>').join("")+(mod==="estoque"?'<td><span class="status-pill '+(Number(r.quantidade)<=Number(r.minimo)?'stock-low':'')+'">'+(Number(r.quantidade)<=Number(r.minimo)?'Estoque baixo':'Normal')+'</span></td>':'')+'<td><button class="button button-quiet button-small" data-delete="'+esc(r.id)+'" type="button" aria-label="Excluir registro '+esc(r.id)+'">Excluir</button></td></tr>').join(""):'<tr><td colspan="'+(cfg.fields.length+2)+'" class="empty-state">Nenhum registro encontrado.</td></tr>'}
 form.addEventListener("submit",e=>{e.preventDefault();const data=Object.fromEntries(new FormData(form));if(cfg.fields.some(([id,,type])=>!String(data[id]||"").trim()||(type==="number"&&(!Number.isFinite(Number(data[id]))||Number(data[id])<0)))){feedback.textContent="Confira os campos obrigatórios e os valores.";feedback.className="form-feedback error";return}const db=read();db[mod]=Array.isArray(db[mod])?db[mod]:[];db[mod].push({id:Math.max(0,...db[mod].map(x=>Number(x.id)||0))+1,...data,criadoEm:new Date().toISOString()});if(write(db)){form.reset();feedback.textContent="Registro salvo neste navegador.";feedback.className="form-feedback";draw()}else{feedback.textContent="Não foi possível salvar neste navegador.";feedback.className="form-feedback error"}});
 root.querySelector("#module-rows").addEventListener("click",e=>{const b=e.target.closest("[data-delete]");if(!b)return;if(!confirm("Deseja excluir este registro demonstrativo?"))return;const db=read();db[mod]=(db[mod]||[]).filter(x=>String(x.id)!==b.dataset.delete);write(db);draw()});filter.addEventListener("input",draw);draw();
}
function renderReports(root){
 const c=rows("consultas"),s=rows("estoque"),h=rows("historico"),p=(()=>{try{const data=JSON.parse(localStorage.getItem(PATIENTS_KEY)||"[]");return Array.isArray(data)?data:[]}catch{return []}})();
 const low=s.filter(x=>Number(x.quantidade)<=Number(x.minimo)).length;
 root.innerHTML='<div class="report-grid">'+[['Pacientes',p.length],['Consultas',c.length],['Itens em estoque',s.length],['Estoque baixo',low],['Registros de histórico',h.length]].map(([a,b])=>'<article class="stat-card"><span class="stat-label">'+a+'</span><strong>'+b+'</strong></article>').join("")+'</div><div class="report-actions"><button class="button button-primary" id="print-report" type="button">Imprimir / salvar em PDF</button></div><p class="muted">Os indicadores refletem somente os dados locais deste navegador. Não representam dados oficiais.</p>';
 root.querySelector("#print-report").addEventListener("click",()=>window.print());
}
function renderBackups(root,cfg){
 root.innerHTML='<div class="content-card backup-panel"><h2>Registro de backup demonstrativo</h2><p class="muted">O navegador não consegue gerar uma cópia confiável do banco de dados. Registre aqui uma atividade simulada; o backup real deverá ser executado no servidor.</p><form id="backup-form" class="module-form"><div class="module-fields">'+cfg.fields.map(([id,label])=>'<div class="field"><label for="b-'+id+'">'+label+' *</label><input id="b-'+id+'" name="'+id+'" required></div>').join("")+'</div><div class="form-actions"><button class="button button-primary">Registrar atividade</button></div><p id="backup-feedback" class="form-feedback" role="status"></p></form><h2>Histórico de atividades</h2><div id="backup-list"></div></div>';
 const form=root.querySelector("#backup-form");function draw(){const list=rows("backups");root.querySelector("#backup-list").innerHTML=list.length?'<ul class="backup-list">'+list.map(x=>'<li><strong>'+esc(x.descricao)+'</strong> — '+esc(x.responsavel)+' <small>'+new Date(x.criadoEm).toLocaleString("pt-BR")+'</small></li>').join("")+'</ul>':'<p class="empty-state">Nenhuma atividade registrada.</p>'}
 form.addEventListener("submit",e=>{e.preventDefault();const d=Object.fromEntries(new FormData(form)),db=read();db.backups=rows("backups");db.backups.push({id:Math.max(0,...db.backups.map(x=>x.id))+1,...d,criadoEm:new Date().toISOString()});const ok=write(db);root.querySelector("#backup-feedback").textContent=ok?"Atividade registrada.":"Falha ao registrar.";if(ok){form.reset();draw()}});draw();
}
document.addEventListener("DOMContentLoaded", async () => {
  try {
    const resposta = await fetch("/api/auth/me", { credentials: "same-origin" });
    if (!resposta.ok) {
      window.location.replace("../");
      return;
    }
    init();
  } catch (erro) {
    window.location.replace("../");
  }
});
})();