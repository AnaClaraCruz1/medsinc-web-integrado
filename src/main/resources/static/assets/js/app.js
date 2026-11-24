const STORAGE_KEY = "medsinc-web-pacientes-v2";
let pacientesEmMemoria = null;

const pacientesIniciais = [
  { id: 1, nome: "Mariana Oliveira Santos", cpf: "529.982.247-25", nascimento: "1988-04-12", telefone: "(31)98888-1200", email: "mariana.exemplo@email.com", endereco: "Rua das Acácias, 120 - Centro", convenio: "Unimed" },
  { id: 2, nome: "Rafael Costa Almeida", cpf: "111.444.777-35", nascimento: "1979-09-23", telefone: "(31)97777-2300", email: "", endereco: "Av. Brasil, 450 - Funcionários", convenio: "Particular" },
  { id: 3, nome: "Camila Ferreira Lima", cpf: "935.411.347-80", nascimento: "1995-02-08", telefone: "(31)96666-3400", email: "camila.exemplo@email.com", endereco: "Rua Ipê Amarelo, 35 - Santa Efigênia", convenio: "Amil" }
];

function obterPacientes() {
  if (pacientesEmMemoria) return pacientesEmMemoria;
  try {
    const dados = JSON.parse(localStorage.getItem(STORAGE_KEY));
    if (Array.isArray(dados) && dados.every(p => p && Number.isInteger(Number(p.id)) && typeof p.nome === "string")) {
      pacientesEmMemoria = dados;
      return pacientesEmMemoria;
    }
    pacientesEmMemoria = [...pacientesIniciais];
    localStorage.setItem(STORAGE_KEY, JSON.stringify(pacientesEmMemoria));
  } catch (erro) {
    console.warn("Armazenamento local indisponível; os dados ficarão apenas nesta sessão.", erro);
    pacientesEmMemoria = [...pacientesIniciais];
  }
  return pacientesEmMemoria;
}

function salvarPacientes(pacientes) {
  pacientesEmMemoria = pacientes;
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(pacientes));
    return true;
  } catch (erro) {
    console.warn("Não foi possível persistir os dados neste navegador.", erro);
    return false;
  }
}

function calcularIdade(dataNascimento) {
  if (!dataNascimento) return "—";
  const [ano, mes, dia] = dataNascimento.split("-").map(Number);
  const hoje = new Date();
  let idade = hoje.getFullYear() - ano;
  if (hoje.getMonth() + 1 < mes || (hoje.getMonth() + 1 === mes && hoje.getDate() < dia)) idade--;
  return Number.isFinite(idade) && idade >= 0 ? idade : "—";
}

function formatarData(data) {
  if (!data || !/^\d{4}-\d{2}-\d{2}$/.test(data)) return "—";
  const [ano, mes, dia] = data.split("-");
  return dia + "/" + mes + "/" + ano;
}

function somenteNumeros(valor) {
  return String(valor || "").replace(/\D/g, "");
}

function formatarCPF(valor) {
  const numeros = somenteNumeros(valor).slice(0, 11);
  return numeros.replace(/(\d{3})(\d)/, "$1.$2").replace(/(\d{3})(\d)/, "$1.$2").replace(/(\d{3})(\d{1,2})$/, "$1-$2");
}

function formatarTelefone(valor) {
  const n = somenteNumeros(valor).slice(0, 11);
  if (n.length <= 2) return n.length ? "(" + n : "";
  if (n.length <= 10) return n.replace(/^(\d{2})(\d{0,4})(\d{0,4}).*/, (_, d, a, b) => "(" + d + ")" + a + (b ? "-" + b : ""));
  return n.replace(/^(\d{2})(\d{5})(\d{0,4}).*/, "($1)$2-$3");
}

function exibirErro(campo, mensagem) {
  const elemento = document.querySelector('[data-error-for="' + campo + '"]');
  const wrapper = elemento?.closest(".field");
  if (elemento) elemento.textContent = mensagem;
  wrapper?.classList.toggle("invalid", Boolean(mensagem));
}

function limparErros() {
  document.querySelectorAll(".field-error").forEach(el => { el.textContent = ""; });
  document.querySelectorAll(".field.invalid").forEach(el => el.classList.remove("invalid"));
}

function configurarLogin() {
  const form = document.getElementById("login-form");
  if (!form) return;
  form.addEventListener("submit", evento => {
    evento.preventDefault();
    const email = document.getElementById("login-email").value.trim();
    const senha = document.getElementById("login-password").value;
    const feedback = document.getElementById("login-feedback");
    if (!email || !senha) {
      feedback.textContent = "Preencha o e-mail e a senha para continuar.";
      feedback.className = "form-feedback error";
      return;
    }
    feedback.textContent = "Acesso demonstrativo liberado.";
    feedback.className = "form-feedback";
    window.location.href = "dashboard.html";
  });
}

function configurarCadastro() {
  const form = document.getElementById("patient-form");
  if (!form) return;
  const cpfInput = document.getElementById("patient-cpf");
  const telefoneInput = document.getElementById("patient-phone");
  cpfInput.addEventListener("input", () => { cpfInput.value = formatarCPF(cpfInput.value); });
  telefoneInput.addEventListener("input", () => { telefoneInput.value = formatarTelefone(telefoneInput.value); });
  form.addEventListener("reset", () => {
    setTimeout(() => { limparErros(); const feedback = document.getElementById("patient-feedback"); feedback.textContent = ""; feedback.className = "form-feedback"; }, 0);
  });
  form.addEventListener("submit", evento => {
    evento.preventDefault();
    limparErros();
    const dados = Object.fromEntries(new FormData(form).entries());
    let valido = true;
    const falhar = (campo, mensagem) => { exibirErro(campo, mensagem); valido = false; };
    if (!dados.nome.trim()) falhar("nome", "Informe o nome completo.");
    if (dados.nome.trim().length > 55) falhar("nome", "Use no máximo 55 caracteres.");
    if (obterPacientes().some(p => somenteNumeros(p.cpf) === somenteNumeros(dados.cpf))) falhar("cpf", "Este CPF já está cadastrado.");
    if (!dados.nascimento) falhar("nascimento", "Informe a data de nascimento.");
    else {
      const data = new Date(dados.nascimento + "T00:00:00");
      if (Number.isNaN(data.getTime()) || data > new Date()) falhar("nascimento", "Informe uma data válida, que não seja futura.");
    }
    const telefone = somenteNumeros(dados.telefone);
    if (telefone.length < 10 || telefone.length > 11) falhar("telefone", "Informe um telefone com DDD.");
    if (dados.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(dados.email)) falhar("email", "Informe um e-mail válido.");
    if (!dados.endereco.trim()) falhar("endereco", "Informe o endereço.");
    if (dados.endereco.trim().length > 200) falhar("endereco", "Use no máximo 200 caracteres.");
    if (!dados.convenio) falhar("convenio", "Selecione um convênio.");
    const feedback = document.getElementById("patient-feedback");
    if (!valido) {
      feedback.textContent = "Revise os campos destacados antes de salvar.";
      feedback.className = "form-feedback error";
      return;
    }
    const pacientes = obterPacientes();
    const novo = { id: pacientes.reduce((maior, p) => Math.max(maior, Number(p.id) || 0), 0) + 1, ...dados, nome: dados.nome.trim(), cpf: formatarCPF(dados.cpf), endereco: dados.endereco.trim(), telefone: formatarTelefone(dados.telefone) };
    pacientes.push(novo);
    const persistido = salvarPacientes(pacientes);
    form.reset();
    feedback.textContent = persistido
      ? "Paciente cadastrado nesta demonstração com sucesso."
      : "Cadastro mantido apenas nesta sessão; o navegador não permitiu salvar os dados.";
    feedback.className = "form-feedback";
  });
}

function escaparHTML(valor) {
  return String(valor ?? "").replace(/[&<>"']/g, caractere => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[caractere]);
}

function configurarConsulta() {
  const tabela = document.getElementById("patients-table");
  if (!tabela) return;
  const campo = document.getElementById("patient-search");
  const contador = document.getElementById("result-count");
  const feedback = document.getElementById("search-feedback");
  const renderizar = () => {
    const termo = campo.value.trim().toLocaleLowerCase("pt-BR");
    const registros = obterPacientes().filter(p => {
      if (!termo) return true;
      const buscaNumerica = somenteNumeros(termo);
      const nome = String(p.nome || "").toLocaleLowerCase("pt-BR");
      return String(p.id).includes(termo)
        || (buscaNumerica.length > 0 && somenteNumeros(p.cpf).includes(buscaNumerica))
        || nome.includes(termo);
    });
    contador.textContent = registros.length + (registros.length === 1 ? " registro encontrado" : " registros encontrados");
    tabela.innerHTML = registros.length ? registros.map(p => '<tr><td class="table-id">#' + escaparHTML(p.id) + '</td><td class="table-name">' + escaparHTML(p.nome) + '</td><td>' + escaparHTML(p.cpf) + '</td><td>' + escaparHTML(formatarData(p.nascimento)) + '</td><td>' + escaparHTML(calcularIdade(p.nascimento)) + '</td><td>' + escaparHTML(p.telefone) + '</td><td><span class="status-pill">' + escaparHTML(p.convenio) + '</span></td></tr>').join("") : '<tr><td colspan="7" class="empty-state">Nenhum paciente corresponde à pesquisa.</td></tr>';
    feedback.textContent = termo ? "Filtro aplicado aos registros demonstrativos." : "";
  };
  campo.addEventListener("input", renderizar);
  document.getElementById("clear-search").addEventListener("click", () => { campo.value = ""; renderizar(); campo.focus(); });
  renderizar();
}

function configurarPainel() {
  const contador = document.getElementById("dashboard-count");
  if (contador) contador.textContent = obterPacientes().length;
}

document.addEventListener("DOMContentLoaded", () => {
  configurarLogin();
  configurarCadastro();
  configurarConsulta();
  configurarPainel();
});
