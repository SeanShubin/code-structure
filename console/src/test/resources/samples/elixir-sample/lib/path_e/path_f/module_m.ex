defmodule PathE.PathF.ModuleM do
  def function_m do
    PathE.PathF.ModuleN.function_n()
    ModuleE.function_e()
  end
end
